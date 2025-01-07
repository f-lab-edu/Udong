package com.hyun.udong.auth.presentation.dto;

public record AccessTokenResponse(String accessToken, long expiredTime) {
}
