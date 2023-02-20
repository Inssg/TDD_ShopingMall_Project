package org.inssg.backend.auth;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private String secretKey;
    private String base64EncodedSecretKey;

    @BeforeEach
    void init() {
        jwtTokenProvider = new JwtTokenProvider();
        secretKey = "ksjdfiojiow1k12ji124y12746y!!@33";
        base64EncodedSecretKey = jwtTokenProvider.encodedBase64SecretKey(secretKey);

    }

    @Test
    @DisplayName("AccessToken 정상 발급")
    void test_() {
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("username", "abc@gmail.com");
        claims.put("roles", "ROLE_USER");

        String subject = "test access token";
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 10);
        Date expiration = calendar.getTime();

        String accessToken = jwtTokenProvider.createAccessToken(claims, subject, expiration,base64EncodedSecretKey);

        assertThat(accessToken).isNotNull();
        System.out.println(accessToken);
    }
}
