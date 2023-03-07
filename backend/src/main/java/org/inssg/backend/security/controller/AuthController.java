package org.inssg.backend.security.controller;

import lombok.RequiredArgsConstructor;
import org.inssg.backend.security.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/logiout")
    public ResponseEntity logout(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization").substring(7);
        String refreshToken = request.getHeader("RefreshToken");

        authService.

    }
}
