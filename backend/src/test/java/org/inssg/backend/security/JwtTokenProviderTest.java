package org.inssg.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import org.inssg.backend.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JwtTokenProviderTest {
    @Autowired
    JwtTokenProvider jwtTokenProvider;

    private String secretKey;
    private String base64EncodedSecretKey;

    @BeforeAll
    void init() {
        secretKey = jwtTokenProvider.getSecretKey();
        base64EncodedSecretKey= jwtTokenProvider.encodedBase64SecretKey(secretKey);
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
        assertDoesNotThrow(() -> jwtTokenProvider.getClaims(accessToken));
    }

    @Test
    @DisplayName("Throw ExpiredJwtException when jws verify")
    void verifyExpirationTest() throws InterruptedException {
        String accessToken = getAccessToken(Calendar.SECOND, 1);

        assertDoesNotThrow(()->jwtTokenProvider.getClaims(accessToken));

        TimeUnit.MILLISECONDS.sleep(1500);

        assertThrows(ExpiredJwtException.class, () -> jwtTokenProvider.getClaims(accessToken));
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
