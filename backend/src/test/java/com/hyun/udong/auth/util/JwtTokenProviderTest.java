package com.hyun.udong.auth.util;

import com.hyun.udong.auth.exception.ExpiredTokenException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtTokenProviderTest {

    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void 유효한_ID로_액세스_토큰을_생성하면_토큰을_반환한다() {
        String token = jwtTokenProvider.generateAccessToken(1L);
        assertNotNull(token);
    }

    @Test
    void 유효한_ID로_리프레시_토큰을_생성하면_토큰을_반환한다() {
        String token = jwtTokenProvider.generateRefreshToken(1L);
        assertNotNull(token);
    }

    @Test
    void 유효한_토큰에서_회원_ID를_추출하면_ID를_반환한다() {
        String token = jwtTokenProvider.generateAccessToken(1L);
        String memberId = jwtTokenProvider.getSubjectFromToken(token);
        assertEquals(((Long) 1L).toString(), memberId);
    }

    @Test
    void 유효하지_않은_토큰에서_회원_ID를_추출하면_예외를_던진다() {
        assertThrows(Exception.class, () -> jwtTokenProvider.getSubjectFromToken("invalid.token"));
    }

    @Test
    void 만료된_토큰을_검증하면_ExpiredTokenException을_던진다() {
        String expiredToken = Jwts.builder()
                .setSubject("1")
                .setIssuedAt(new Date(System.currentTimeMillis() - 2000))
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .compact();

        assertThrows(ExpiredTokenException.class, () -> jwtTokenProvider.getSubjectFromToken(expiredToken));
    }
}
