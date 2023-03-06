package org.inssg.backend.security;

import org.inssg.backend.member.Member;
import org.inssg.backend.member.MemberCreate;
import org.inssg.backend.member.MemberRepository;
import org.inssg.backend.security.jwt.JwtTokenProvider;
import org.inssg.backend.security.refreshtoken.RefreshToken;
import org.inssg.backend.security.refreshtoken.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class AuthServiceTest {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private AuthService authService;

    String accessToken;
    String refreshToken;
    String email;
    String password;
    String username;


    @BeforeEach
    void setUp() {
        email = "abc@gmail.com";
        password = "1234";
        username = "테스트";

        MemberCreate memberCreate = new MemberCreate(email, password, username);
        Member member = Member.create(memberCreate, passwordEncoder);
        memberRepository.save(member);

        accessToken = jwtTokenProvider.createAccessToken(member);
        refreshToken = jwtTokenProvider.createRefreshToken(member);
        RefreshToken token = RefreshToken.builder().email(email).value(refreshToken).build();
        refreshTokenRepository.save(token);
    }

    @Test
    @DisplayName("reissue 성공")
    void test_reissue() {
        Map<String, Object> reissuedToken = authService.reissue(accessToken, refreshToken);

        assertThat(reissuedToken.get("accessToken")).isNotNull();
        assertThat(reissuedToken.get("refreshToken")).isNotNull();
        assertThat(reissuedToken.get("accessToken")).isNotEqualTo(accessToken);
        assertThat(reissuedToken.get("refreshToken")).isNotEqualTo(refreshToken);
        assertThat(refreshTokenRepository.findByKey(email)).isNotEqualTo(refreshToken);
    }

    //Todo: Redis 활용 로그아웃 기능 구현 필요
    @Test
    void test_logout() {
        authService.logout(accessToken, refreshToken);
        //기대하는것 로그아웃 후, 레디스에서 refresthToken 조회시 없음
        //레디스에 로그아웃한 accesstoken 정상적으로 블랙리스트에 추가도있는지
        //블랙리스트 accessToken으로 로그인 시도하면,
    }

}
