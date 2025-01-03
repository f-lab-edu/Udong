package com.hyun.udong.auth.presentation.controller;

import com.hyun.udong.auth.application.service.AuthService;
import com.hyun.udong.auth.presentation.dto.AccessTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;


    @GetMapping("/login/kakao")
    public ResponseEntity<AccessTokenResponse> kakaoLogin(@RequestParam("code") final String code) {
        AccessTokenResponse accessToken = authService.kakaoLogin(code);
        return ResponseEntity.ok().body(accessToken);
    }
}
