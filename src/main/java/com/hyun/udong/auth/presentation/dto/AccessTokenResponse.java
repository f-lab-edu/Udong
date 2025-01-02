package com.hyun.udong.auth.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AccessTokenResponse {
    private final String accessToken;
    private final long expiredTime;

    public String getAccessToken() {
        return accessToken;
    }

    public long getExpiredTime() {
        return expiredTime;
    }
}
