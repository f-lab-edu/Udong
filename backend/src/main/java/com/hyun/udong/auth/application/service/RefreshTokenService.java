package com.hyun.udong.auth.application.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class RefreshTokenService {
    private final StringRedisTemplate redisTemplate;

    public RefreshTokenService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(String refreshToken, String userId, LocalDateTime expiresAt) {
        long ttlMillis = Duration.between(LocalDateTime.now(), expiresAt).toMillis();
        if (ttlMillis > 0) {
            redisTemplate.opsForValue().set(refreshToken, userId, ttlMillis, TimeUnit.MILLISECONDS);
        }
    }

    public void delete(String refreshToken) {
        redisTemplate.delete(refreshToken);
    }

    public boolean isValid(String refreshToken) {
        return redisTemplate.hasKey(refreshToken);
    }

    public String getUserId(String refreshToken) {
        return redisTemplate.opsForValue().get(refreshToken);
    }

    public void addToBlacklist(String refreshToken, long ttlMillis) {
        String blacklistKey = "blacklist:" + refreshToken;
        redisTemplate.opsForValue().set(blacklistKey, "logout", ttlMillis, TimeUnit.MILLISECONDS);
    }

    public boolean isBlacklisted(String refreshToken) {
        String blacklistKey = "blacklist:" + refreshToken;
        return redisTemplate.hasKey(blacklistKey);
    }
}
