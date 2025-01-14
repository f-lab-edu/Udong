package com.hyun.udong.auth.util;

import com.hyun.udong.auth.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import static io.jsonwebtoken.Jwts.builder;
import static io.jsonwebtoken.Jwts.parserBuilder;

@Component
public class JwtTokenProvider {
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1_000L * 60 * 60;
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1_000L * 60 * 60 * 24 * 14;

    @Value("${jwt.secret}")
    private String secret;

    private Key getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(Long id) {
        return builder()
                .setSubject(id.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(getSecretKey())
                .compact();
    }

    public String generateRefreshToken(Long id) {
        return builder()
                .setSubject(id.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(getSecretKey())
                .compact();
    }

    public long getAccessTokenExpireTime() {
        return ACCESS_TOKEN_EXPIRE_TIME;
    }

    public long getRefreshTokenExpireTime() {
        return REFRESH_TOKEN_EXPIRE_TIME;
    }

    public Jws<Claims> parseToken(String token) {
        try {
            return parserBuilder().setSigningKey(getSecretKey()).build().parseClaimsJws(token);
        } catch (Exception e) {
            throw InvalidTokenException.EXCEPTION;
        }
    }

    public String getMemberIdFromToken(String token) {
        return parseToken(token).getBody().getSubject();
    }
}

