package com.hyun.udong.common.fixture;

import com.hyun.udong.auth.util.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestOauth {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public static String ACCESS_TOKEN_NOT_EXIST = "Bearer " + "not_exist_token";

    public String generateAccessToken(Long memberId) {
        return "Bearer " + jwtTokenProvider.generateAccessToken(memberId);
    }
}
