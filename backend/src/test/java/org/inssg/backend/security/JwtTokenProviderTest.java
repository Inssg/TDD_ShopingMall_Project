package org.inssg.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    @DisplayName("Secretkey base64 decode Test")
    public void encodeBase64SecretKeyTest() {
        assertThat(secretKey).isEqualTo(new String(Decoders.BASE64.decode(base64EncodedSecretKey)));
    }

    @Test
    @DisplayName("AccessToken 정상 발급")
    void generateAccessTokenTest() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", "abc@gmail.com");
        claims.put("roles", "ROLE_USER");

        String subject = "test access token";
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 10);
        Date expiration = calendar.getTime();

        String accessToken = jwtTokenProvider.accessTokenAssembly(claims, subject, expiration,base64EncodedSecretKey);

        assertThat(accessToken).isNotNull();
        System.out.println(accessToken);
    }

    @Test
    @DisplayName("RefreshToken 정상 발급")
    void generateRefreshTokenTest() {
        String subject = "test refresh Token";

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 24);
        Date expiration = calendar.getTime();

        String refreshToken = jwtTokenProvider.refreshTokenAssembly(subject, expiration, base64EncodedSecretKey);

        assertThat(refreshToken).isNotNull();
        System.out.println(refreshToken);
    }

    @Test
    @DisplayName("jws 검증 성공")
    void verifySignatureTest() {
        String accessToken = getAccessToken(Calendar.MINUTE, 10);
        assertDoesNotThrow(() -> jwtTokenProvider.verifySignature(accessToken, base64EncodedSecretKey));
    }

    @Test
    @DisplayName("Throw ExpiredJwtException when jws verify")
    void verifyExpirationTest() throws InterruptedException {
        String accessToken = getAccessToken(Calendar.SECOND, 1);

        assertDoesNotThrow(()->jwtTokenProvider.verifySignature(accessToken,base64EncodedSecretKey));

        TimeUnit.MILLISECONDS.sleep(1500);

        assertThrows(ExpiredJwtException.class, () -> jwtTokenProvider.verifySignature(accessToken, base64EncodedSecretKey));
    }

    @Test
    @DisplayName("AccessToken claims 내용 확인")
    void checkClaimsTest() {
        String accessToken = getAccessToken(Calendar.MINUTE, 1);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtTokenProvider.getKeyFromBase64EncodedKey(base64EncodedSecretKey))
                .build()
                .parseClaimsJws(accessToken)
                .getBody();

        String username = claims.get("username", String.class);
        String roles = claims.get("roles",String.class);

        assertThat(username).isEqualTo("abc@gmail.com");
        assertThat(roles).isEqualTo("ROLE_USER");
    }

    public String getAccessToken(int timeUnit, int timeAmount) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", "abc@gmail.com");
        claims.put("roles", "ROLE_USER");

        String subject = "test access token verify";
        Calendar calendar = Calendar.getInstance();
        calendar.add(timeUnit, timeAmount);
        Date expiration = calendar.getTime();
        String accessToken = jwtTokenProvider.accessTokenAssembly(claims, subject, expiration, base64EncodedSecretKey);

        return accessToken;
    }
}
