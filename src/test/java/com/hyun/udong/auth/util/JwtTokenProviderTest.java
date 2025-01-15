package com.hyun.udong.auth.util;

import com.hyun.udong.auth.exception.ExpiredTokenException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;

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

    @Test
    @DisplayName("만료된 토큰을 검증하면 ExpiredTokenException을 던진다.")
    void parseToken_expiredToken_throwsExpiredTokenException() {
        String expiredToken = Jwts.builder()
                .setSubject("1")
                .setIssuedAt(new Date(System.currentTimeMillis() - 2000))
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .compact();

        assertThrows(ExpiredTokenException.class, () -> jwtTokenProvider.getMemberIdFromToken(expiredToken));
    }

    @Test
    @DisplayName("액세스 토큰과 리프레시 토큰의 만료 시간(1시간 뒤, 2주 뒤)을 검증한다.")
    void validateTokenExpiryTimes() {
        // given
        Long memberId = 1L;
        String accessToken = jwtTokenProvider.generateAccessToken(memberId);
        String refreshToken = jwtTokenProvider.generateRefreshToken(memberId);

        // when
        LocalDateTime accessTokenExpireTime = jwtTokenProvider.getTokenExpireTime(accessToken);
        LocalDateTime refreshTokenExpireTime = jwtTokenProvider.getTokenExpireTime(refreshToken);

        // then
        LocalDateTime now = LocalDateTime.now();
        assertEquals(now.plusHours(1).withNano(0), accessTokenExpireTime.withNano(0));
        assertEquals(now.plusDays(14).withNano(0), refreshTokenExpireTime.withNano(0));
    }

}
