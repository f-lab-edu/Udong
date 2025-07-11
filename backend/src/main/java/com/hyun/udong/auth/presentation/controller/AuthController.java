package com.hyun.udong.auth.presentation.controller;

import com.hyun.udong.auth.application.service.AuthService;
import com.hyun.udong.auth.presentation.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/oauth/kakao")
    public ResponseEntity<LoginResponse> kakaoLogin(@RequestParam("code") final String code) {
        LoginResponse loginResponse = authService.kakaoLogin(code);
        return ResponseEntity.ok().body(loginResponse);
    }

    @GetMapping("/token/refresh")
    public ResponseEntity<LoginResponse> refreshTokens(@RequestParam("refreshToken") final String refreshToken) {
        LoginResponse loginResponse = authService.refreshTokens(refreshToken);
        return ResponseEntity.ok().body(loginResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestParam("refreshToken") final String refreshToken) {
        authService.logout(refreshToken);
        return ResponseEntity.noContent().build();
    }
}
