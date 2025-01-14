package com.hyun.udong.auth.presentation.dto;

public record AuthTokens(String accessToken, long accessTokenAge, String refreshToken, long refreshTokenAge) {
}
