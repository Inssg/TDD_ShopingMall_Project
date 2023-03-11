package org.inssg.backend.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.inssg.backend.member.Member;
import org.inssg.backend.security.exception.MemberAlreadyLoggedIn;
import org.inssg.backend.security.jwt.JwtTokenProvider;
import org.inssg.backend.security.dto.LoginDto;
import org.inssg.backend.security.redis.RedisService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;


    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        //LoginDto 클래스로 역직렬화
        ObjectMapper mapper = new ObjectMapper();
        LoginDto loginDto = mapper.readValue(request.getInputStream(), LoginDto.class);

        if(redisService.getValues(loginDto.getUserName()) != null){
            throw new MemberAlreadyLoggedIn();
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDto.getUserName(), loginDto.getPassword());
        //authenticationManager 에게 인증위임
        return authenticationManager.authenticate(authenticationToken);
    }

    //인증에 성공할 경우 호출됨
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        Member member = (Member) authResult.getPrincipal();

        String accessToken = jwtTokenProvider.createAccessToken(member);
        String refreshToken = jwtTokenProvider.createRefreshToken(member);

        // 인증 성공시 RefreshToken Redis에 저장(expiration 설정을 통해 자동 삭제 처리)
        redisService.setValues(member.getEmail(), refreshToken, jwtTokenProvider.getRefreshTokenExpirationMinutes());

        response.setHeader("Authorization", "Bearer " + accessToken);
        response.setHeader("RefreshToken", refreshToken);

        this.getSuccessHandler().onAuthenticationSuccess(request,response,authResult);
    }
}
