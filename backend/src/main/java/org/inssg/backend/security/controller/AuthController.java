package org.inssg.backend.security.controller;

import lombok.RequiredArgsConstructor;
import org.inssg.backend.security.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/logout")
    public ResponseEntity logout(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization").substring(7);
        String refreshToken = request.getHeader("RefreshToken");

        authService.logout(accessToken,refreshToken);

        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/reissue")
    public ResponseEntity reissue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = request.getHeader("RefreshToken");

        Map<String, String> reissueToken = authService.reissue(refreshToken);
        response.setHeader("Authorization", "Bearer " + reissueToken.get("accessToken"));
        response.setHeader("RefreshToken", reissueToken.get("refreshToken"));

        return new ResponseEntity(HttpStatus.OK);
    }
}
