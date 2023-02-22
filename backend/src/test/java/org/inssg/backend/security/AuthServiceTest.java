package org.inssg.backend.security;

import org.inssg.backend.member.Member;
import org.inssg.backend.member.MemberNotFound;
import org.inssg.backend.member.MemberRepository;
import org.inssg.backend.security.jwt.JwtTokenProvider;
import org.inssg.backend.security.refreshtoken.RefreshToken;
import org.inssg.backend.security.refreshtoken.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class AuthServiceTest {


    JwtTokenProvider jwtTokenProvider;

    MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    private AuthService authService;
    private RefreshTokenRepository refreshTokenRepository;
    private MemberRepository memberRepository;

    // Todo:accessToken refreshToken request Header에 추가
    @Test
    void test_reissue() {
        String accessToken = mockRequest.getHeader("Authorization").substring(7);
        String refreshToken = mockRequest.getHeader("RefreshToken");

        Map<String, Object> reissuedToken = authService.reissue(accessToken, refreshToken);


        assertThat(reissuedToken.get("accessToken")).isNotEqualTo(accessToken);
        assertThat(reissuedToken.get("refreshToken")).isEqualTo(refreshToken);

    }


    private class AuthService {
        public Map<String, Object> reissue(String accessToken, String refreshTokenValue) {
            HashMap<String, Object> reissuedToken = new HashMap<>();
            String email = jwtTokenProvider.getEmailFromRefreshToken(refreshTokenValue);

            RefreshToken refreshToken = refreshTokenRepository.findByKey(email).orElseThrow(() -> new TokenNotExist());
            Member member = memberRepository.findByEmail(email).orElseThrow(() -> new MemberNotFound());

            if (!refreshToken.getValue().equals(refreshTokenValue)) {
                throw new TokenNotAccord();
            }
            String newAccessToken = jwtTokenProvider.createAccessToken(member);
            String newRefreshToken = jwtTokenProvider.createRefreshToken(member);
            reissuedToken.put("accessToken", newAccessToken);
            reissuedToken.put("refreshToken", newRefreshToken);

            refreshToken.updateValue(newRefreshToken);
            refreshTokenRepository.save(refreshToken);

            return reissuedToken;
        }
    }


    }
}
