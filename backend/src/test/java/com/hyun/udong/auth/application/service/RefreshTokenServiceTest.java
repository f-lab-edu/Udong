package com.hyun.udong.auth.application.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RefreshTokenServiceTest {

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    void 저장된_리프레시토큰은_유효하고_삭제하면_무효가_된다() {
        // given
        String refreshToken = UUID.randomUUID().toString();
        String userId = "123";
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(5);

        // when
        refreshTokenService.save(refreshToken, userId, expiresAt);

        // then
        assertThat(refreshTokenService.isValid(refreshToken)).isTrue();
        assertThat(refreshTokenService.getUserId(refreshToken)).isEqualTo(userId);

        // when - 삭제
        refreshTokenService.delete(refreshToken);

        // then
        assertThat(refreshTokenService.isValid(refreshToken)).isFalse();
    }

    @Test
    void 만료된_리프레시토큰은_유효하지_않다() throws InterruptedException {
        // given
        String refreshToken = UUID.randomUUID().toString();
        String userId = "456";
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(1);

        // when
        refreshTokenService.save(refreshToken, userId, expiresAt);

        // then
        assertThat(refreshTokenService.isValid(refreshToken)).isTrue();

        // 1.5초 대기 후 만료 확인
        Thread.sleep(1500);
        assertThat(refreshTokenService.isValid(refreshToken)).isFalse();
    }
}