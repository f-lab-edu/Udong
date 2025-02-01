package com.hyun.udong.common.fixture;

import com.hyun.udong.auth.util.JwtTokenProvider;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestOauth {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public static String ACCESS_TOKEN_1L;

    @PostConstruct
    public void init() {
        ACCESS_TOKEN_1L = "Bearer " + jwtTokenProvider.generateAccessToken(1L);
    }
}
