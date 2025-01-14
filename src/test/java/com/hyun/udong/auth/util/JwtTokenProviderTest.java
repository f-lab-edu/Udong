package com.hyun.udong.auth.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtTokenProviderTest {

    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("유효한 ID로 액세스 토큰을 생성하면 토큰을 반환한다.")
    void generateAccessToken_validId_returnsToken() {
        Long id = 1L;
        String token = jwtTokenProvider.generateAccessToken(id);
        assertNotNull(token);
    }

    @Test
    @DisplayName("유효한 ID로 리프레시 토큰을 생성하면 토큰을 반환한다.")
    void generateRefreshToken_validId_returnsToken() {
        Long id = 1L;
        String token = jwtTokenProvider.generateRefreshToken(id);
        assertNotNull(token);
    }

    @Test
    @DisplayName("유효하지 않은 토큰을 검증하면 false를 반환한다.")
    void parseToken_invalidToken_returnsFalse() {
        String token = "invalid.token";
        assertThrows(Exception.class, () -> jwtTokenProvider.parseToken(token));
    }

    @Test
    @DisplayName("유효한 토큰에서 회원 ID를 추출하면 ID를 반환한다.")
    void getMemberIdFromToken_validToken_returnsId() {
        Long id = 1L;
        String token = jwtTokenProvider.generateAccessToken(id);
        String memberId = jwtTokenProvider.getMemberIdFromToken(token);
        assertEquals(id.toString(), memberId);
    }

    @Test
    @DisplayName("유효하지 않은 토큰에서 회원 ID를 추출하면 예외를 던진다.")
    void getMemberIdFromToken_invalidToken_throwsException() {
        String token = "invalid.token";
        assertThrows(Exception.class, () -> jwtTokenProvider.getMemberIdFromToken(token));
    }
}
