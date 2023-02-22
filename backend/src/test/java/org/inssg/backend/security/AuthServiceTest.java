package org.inssg.backend.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.security.AuthProvider;

public class AuthServiceTest {


    MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    private AuthService authService;
    // Todo:accessToken refreshToken request Header에 추가

    @Test
    void test_logout() {

        String accessToken = mockRequest.getHeader("Authorization").substring(7);
        String refreshToken = mockRequest.getHeader("RefreshToken");

        authService.logout(accessToken, refreshToken);
    }

    private class AuthService {
        public void logout(String accessToken, String refreshToken) {

        }
    }
}
