package com.hyun.udong.auth.presentation.controller;

import com.hyun.udong.auth.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class JwtTokenTestController {

    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/generate-token")
    public String generateToken(@RequestParam("userId") Long userId) {
        return jwtTokenProvider.generateAccessToken(userId);
    }
}
