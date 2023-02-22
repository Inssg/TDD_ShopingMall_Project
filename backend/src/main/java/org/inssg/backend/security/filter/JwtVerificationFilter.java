package org.inssg.backend.security.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inssg.backend.security.jwt.JwtTokenProvider;
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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            Map<String, Object> claims = verifyJws(request);
            setAuthenticationToContext(claims);
        } catch (SignatureException se) {
            request.setAttribute("exception", se);
        } catch (ExpiredJwtException ee) {
            request.setAttribute("exception", ee);
        } catch (Exception e) {
            request.setAttribute("exception",e);
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
    private Map<String, Object> verifyJws(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization").substring(7);
        String base64encodedSecretKey = jwtTokenProvider.encodedBase64SecretKey(jwtTokenProvider.getSecretKey());
        Map<String, Object> claims = jwtTokenProvider.getClaims(accessToken, base64encodedSecretKey).getBody();
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
