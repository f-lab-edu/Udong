package com.hyun.udong.auth.presentation.dto;

import java.time.LocalDateTime;

public record AuthTokens(String accessToken, LocalDateTime accessTokenExpDate, String refreshToken,
                         LocalDateTime refreshTokenExpDate) {
}
