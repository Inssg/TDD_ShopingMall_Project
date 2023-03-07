package org.inssg.backend.security.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inssg.backend.security.TokenNotValid;
import org.inssg.backend.security.jwt.JwtTokenProvider;
import org.inssg.backend.security.redis.RedisService;
import org.inssg.backend.security.userdetails.MemberDetails;
import org.inssg.backend.security.userdetails.MemberDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Map;

// 토큰을 검증하는곳. (로그인 이후라면 요청마다 들르게 되는 곳)
@RequiredArgsConstructor
public class JwtVerificationFilter extends OncePerRequestFilter { //request당 한번만 실행
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberDetailsService memberDetailsService;
    private final RedisService redisService;

    //Todo: JwtTokenProvider getClaims에서 Excetption Throw 하는 선택지도있다.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            String accessToken = request.getHeader("Authorization").substring(7);
            Map<String, Object> claims = verifyJws(request,accessToken);

            if (redisService.hasKeyBlackList(accessToken)) {
                throw new TokenNotValid();
            }
            setAuthenticationToContext(claims);
        } catch (SignatureException se) {
            request.setAttribute("exception", se);
        } catch (ExpiredJwtException ee) {
            request.setAttribute("exception", ee);
        } catch (MalformedJwtException me) {
            request.setAttribute("exception", me);
        } catch (Exception e) {
            request.setAttribute("exception", e);
        }

        filterChain.doFilter(request,response);
    }
    // true라면 현재 필터 검증하지 않고 다음 필터로 넘어간다.
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String authorization = request.getHeader("Authorization");
        return authorization == null || !authorization.startsWith("Bearer");
    }
    // 토큰을 검증하고 클레임생성
    private Map<String, Object> verifyJws(HttpServletRequest request, String accessToken) {
        Map<String, Object> claims = jwtTokenProvider.getClaims(accessToken).getBody();
        return claims;
    }

//Todo: AuthorityUtils로 로직 변경 필요
    private void setAuthenticationToContext(Map<String, Object> claims) {
        String username = (String) claims.get("username");
        MemberDetails memberDetails = (MemberDetails) memberDetailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(memberDetails, null, memberDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
