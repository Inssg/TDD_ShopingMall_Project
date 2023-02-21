package org.inssg.backend.security.filter;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.inssg.backend.security.jwt.JwtTokenProvider;
import org.inssg.backend.security.userdetails.MemberDetails;
import org.inssg.backend.security.userdetails.MemberDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
public class JwtVerificationFilter extends OncePerRequestFilter { //request당 한번만 실행
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberDetailsService memberDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Map<String, Object> claims = verifyJws(request);
        setAuthenticationToContext(claims);

        filterChain.doFilter(request,response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String authorization = request.getHeader("Authorization");

        return authorization == null || !authorization.startsWith("Bearer");
    }

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

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null, memberDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
