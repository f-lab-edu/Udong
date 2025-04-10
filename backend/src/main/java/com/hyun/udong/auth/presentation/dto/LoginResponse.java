package com.hyun.udong.auth.presentation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginResponse {
    private Long id;
    private String nickname;
    private AuthTokens token;

    public LoginResponse(Long id, String nickname, AuthTokens token) {
        this.id = id;
        this.nickname = nickname;
        this.token = token;
    }
}
