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
    public static String ACCESS_TOKEN_2L;
    public static String ACCESS_TOKEN_3L;
    public static String ACCESS_TOKEN_4L;
    public static String ACCESS_TOKEN_5L;

    public static String ACCESS_TOKEN_NOT_EXIST = "Bearer " + "not_exist_token";

    @PostConstruct
    public void init() {
        ACCESS_TOKEN_1L = "Bearer " + jwtTokenProvider.generateAccessToken(1L);
        ACCESS_TOKEN_2L = "Bearer " + jwtTokenProvider.generateAccessToken(2L);
        ACCESS_TOKEN_3L = "Bearer " + jwtTokenProvider.generateAccessToken(3L);
        ACCESS_TOKEN_4L = "Bearer " + jwtTokenProvider.generateAccessToken(4L);
        ACCESS_TOKEN_5L = "Bearer " + jwtTokenProvider.generateAccessToken(5L);
    }
}
