package com.hyun.udong.auth.util;

import com.hyun.udong.auth.exception.ExpiredTokenException;
import com.hyun.udong.auth.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static io.jsonwebtoken.Jwts.builder;
import static io.jsonwebtoken.Jwts.parserBuilder;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${ACCESS_TOKEN_EXPIRE_TIME}")
    private long accessTokenExpireTime;

    @Value("${REFRESH_TOKEN_EXPIRE_TIME}")
    private long refreshTokenExpireTime;

    @Value("${jwt.secret}")
    private String secret;

    private Key getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }


    public String generateAccessToken(Long id, Date issuedAt) {
        return builder()
                .setSubject(id.toString())
                .setIssuedAt(issuedAt)
                .setExpiration(new Date(issuedAt.getTime() + accessTokenExpireTime))
                .signWith(getSecretKey())
                .compact();
    }

    public String generateRefreshToken(Long id, Date issuedAt) {
        return builder()
                .setSubject(id.toString())
                .setIssuedAt(issuedAt)
                .setExpiration(new Date(issuedAt.getTime() + refreshTokenExpireTime))
                .signWith(getSecretKey())
                .compact();
    }

    public LocalDateTime getTokenExpireTime(String token) {
        Date expiration = parseToken(token).getBody().getExpiration();
        return LocalDateTime.ofInstant(expiration.toInstant(), ZoneId.systemDefault());
    }

    private Jws<Claims> parseToken(String token) {
        try {
            return parserBuilder().setSigningKey(getSecretKey()).build().parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw ExpiredTokenException.EXCEPTION;
        } catch (Exception e) {
            log.error("검증 실패 토큰: {}", token, e);
            throw InvalidTokenException.EXCEPTION;
        }
    }

    public String getSubjectFromToken(String token) {
        return parseToken(token).getBody().getSubject();
    }
}

