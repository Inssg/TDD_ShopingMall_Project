package org.inssg.backend.security;

import org.inssg.backend.member.Member;
import org.inssg.backend.member.MemberCreate;
import org.inssg.backend.member.MemberRepository;
import org.inssg.backend.security.jwt.JwtTokenProvider;
import org.inssg.backend.security.service.AuthService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthServiceTest {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthService authService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    String accessToken;
    String refreshToken;
    String email;
    String password;
    String username;


    @BeforeAll
    void setUp() {
        email = "abc@gmail.com";
        password = "1234";
        username = "테스트";

        MemberCreate memberCreate = new MemberCreate(email, password, username);
        Member member = Member.create(memberCreate, passwordEncoder);
        memberRepository.save(member);

        accessToken = jwtTokenProvider.createAccessToken(member);
        refreshToken = jwtTokenProvider.createRefreshToken(member);
    }

    @Test
    @DisplayName("reissue 성공")
    void test_reissue() {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(email, refreshToken);
        Map<String, String> reissuedToken = authService.reissue(refreshToken);

        assertThat(reissuedToken.get("accessToken")).isNotNull();
        assertThat(reissuedToken.get("refreshToken")).isNotNull();
        assertThat(reissuedToken.get("accessToken")).isNotEqualTo(accessToken);
        assertThat(reissuedToken.get("refreshToken")).isNotEqualTo(refreshToken);
        assertThat(redisTemplate.opsForValue().get(email)).isNotEqualTo(refreshToken);
    }

    @Test
    @DisplayName("logout 성공 - Redis에 AccessToken Blacklist 추가 & RefreshToken 삭제")
    void test_logout() {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(email, refreshToken);

        authService.logout(accessToken, refreshToken);

        assertThat(values.get(email)).isNull();
        assertThat(values.get(accessToken)).isEqualTo("BlackList");

    }


}

