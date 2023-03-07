package org.inssg.backend.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.inssg.backend.member.Authority;
import org.inssg.backend.member.Member;
import org.inssg.backend.member.MemberCreate;
import org.inssg.backend.security.filter.JwtVerificationFilter;
import org.inssg.backend.security.jwt.JwtTokenProvider;
import org.inssg.backend.security.redis.RedisService;
import org.inssg.backend.security.userdetails.MemberDetails;
import org.inssg.backend.security.userdetails.MemberDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
public class JwtVerificationFilterTest {

    MockHttpServletRequest mockRequest;
    MockHttpServletResponse mockResponse;
    FilterChain mockFilterChain;
    BCryptPasswordEncoder passwordEncoder;

    Member member;
    MemberDetailsService mockMemberDetailsService;
    JwtVerificationFilter jwtVerificationFilter;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    RedisService redisService;

    MemberDetails memberDetails;

    String email = "abc@gmail.com";
    String password = "1234";
    String username = "테스트";

    @BeforeEach
    void setUp() {

        mockRequest = new MockHttpServletRequest();
        mockResponse = new MockHttpServletResponse();
        mockFilterChain = Mockito.mock(FilterChain.class);
        passwordEncoder = new BCryptPasswordEncoder();
        mockMemberDetailsService = mock(MemberDetailsService.class);
        jwtVerificationFilter = new JwtVerificationFilter(jwtTokenProvider, mockMemberDetailsService,redisService);

        MemberCreate memberCreate = new MemberCreate(email, password, username);
        member = Member.create(memberCreate, passwordEncoder);
        memberDetails = MemberDetails.of(member);
        String accessToken = jwtTokenProvider.createAccessToken(member);
        mockRequest.addHeader("Authorization", "Bearer " + accessToken);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("토큰 검증 성공->ContextHolder에 토큰 저장 -> loadUserByUsername 메서드 호출,다음 필터 호출 ")
    void test_filter_continuesToNextFilterWithToken() throws ServletException, IOException {

        when(mockMemberDetailsService.loadUserByUsername(anyString()))
                .thenReturn(memberDetails);

        jwtVerificationFilter.doFilter(mockRequest, mockResponse, mockFilterChain);


        verify(mockFilterChain, times(1)).doFilter(mockRequest,mockResponse);
        verify(mockMemberDetailsService, times(1)).loadUserByUsername(anyString());

    }

    @Test
    @DisplayName("토큰없이 요청 -> ShouldNotFilter() 적용, 다음 필터 호출, ContextHolder에 토큰저장 X ")
    void test_filter_continuesToNextFilterWithoutToken() throws ServletException, IOException {
        MockHttpServletRequest requestWithoutToken = new MockHttpServletRequest();

        jwtVerificationFilter.doFilter(requestWithoutToken, mockResponse, mockFilterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(mockFilterChain, times(1)).doFilter(requestWithoutToken,mockResponse);
    }

    @Test
    @DisplayName("유효하지 않은 토큰으로 요청 -> Throws JwtException , ContextHolder에 토큰저장 X")
    void test_filter_InvalidToken_Throws_JwtException() throws ServletException, IOException {
        MockHttpServletRequest invalidRequest = new MockHttpServletRequest();
        invalidRequest.addHeader("Authorization", "Bearer invalidToken1234!");

        jwtVerificationFilter.doFilter(invalidRequest, mockResponse, mockFilterChain);

        Exception exception = (Exception) invalidRequest.getAttribute("exception");

        assertThat(exception.getClass()).isEqualTo(MalformedJwtException.class);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(mockFilterChain, times(1)).doFilter(invalidRequest,mockResponse);
    }

    @Test
    @DisplayName("SecurityContextHolder에 토큰 저장 성공")
    void test_setAuthenticationInSecurityContext() throws ServletException, IOException {
        when(mockMemberDetailsService.loadUserByUsername(anyString()))
                .thenReturn(memberDetails);

        jwtVerificationFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        MemberDetails principal = (MemberDetails) authentication.getPrincipal();
        assertThat(principal.getEmail()).isEqualTo(email);
        assertThat(principal.getRoles().get(0)).isEqualTo("ROLE_USER");
        assertThat(authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"))).isTrue();
    }

}
