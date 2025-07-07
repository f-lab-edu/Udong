package com.hyun.udong.auth.application.service;

import com.hyun.udong.auth.exception.InvalidTokenException;
import com.hyun.udong.auth.infrastructure.client.KakaoOAuthClient;
import com.hyun.udong.auth.presentation.dto.KakaoProfileResponse;
import com.hyun.udong.auth.presentation.dto.KakaoTokenResponse;
import com.hyun.udong.auth.presentation.dto.LoginResponse;
import com.hyun.udong.auth.util.JwtTokenProvider;
import com.hyun.udong.common.util.DataCleanerExtension;
import com.hyun.udong.member.application.service.MemberService;
import com.hyun.udong.member.domain.Member;
import com.hyun.udong.member.domain.SocialType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;

@ExtendWith(DataCleanerExtension.class)
@SpringBootTest
class AuthServiceTest {

    @MockitoBean
    private KakaoOAuthClient kakaoOAuthClient;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MemberService memberService;

    @Autowired
    private AuthService authService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    void 카카오_로그인_시_refreshToken이_Redis에_저장된다() {
        // given
        String code = "authCode";
        KakaoTokenResponse kakaoTokenResponse = new KakaoTokenResponse("accessToken");
        KakaoProfileResponse profile = new KakaoProfileResponse(100L, "hyun", "profile_image");

        given(kakaoOAuthClient.getToken(code)).willReturn(kakaoTokenResponse);
        given(kakaoOAuthClient.getUserProfile(kakaoTokenResponse.getAccessToken())).willReturn(profile);
        given(jwtTokenProvider.generateAccessToken(1L)).willReturn("accessToken");
        given(jwtTokenProvider.generateRefreshToken(1L)).willReturn("refreshToken");
        given(jwtTokenProvider.getTokenExpireTime("refreshToken")).willReturn(LocalDateTime.now().plusMinutes(10));

        // when
        LoginResponse response = authService.kakaoLogin(code);

        // then
        // 1. 응답값 검증
        then(response).isNotNull();
        then(response.getToken().refreshToken()).isEqualTo("refreshToken");
        then(response.getId()).isEqualTo(1L);

        // 2. Redis에 저장된 값 검증
        String redisValue = redisTemplate.opsForValue().get(response.getToken().refreshToken());
        then(redisValue).isEqualTo(String.valueOf(response.getId()));

        // 3. Redis에 실제로 키가 존재하는지
        then(redisTemplate.hasKey(response.getToken().refreshToken())).isTrue();

        // 4. TTL(만료 시간) 검증 (예: 9분 이상 남아있는지)
        Long ttl = redisTemplate.getExpire(response.getToken().refreshToken(), TimeUnit.SECONDS);
        then(ttl).isGreaterThan(500L); // 10분 중 500초(8분 20초) 이상 남아있으면 정상
    }

    @Test
    void refreshToken_재발급_시_기존_토큰은_삭제되고_새_토큰이_Redis에_저장된다() {
        // given
        String oldRefreshToken = "oldRefreshToken";
        String newRefreshToken = "newRefreshToken";

        Member member = memberService.save(new Member(100L, SocialType.KAKAO, "hyun", "profile_image"));
        Long memberId = member.getId();
        refreshTokenService.save(oldRefreshToken, memberId.toString(), LocalDateTime.now().plusMinutes(10));
        given(jwtTokenProvider.getSubjectFromToken(oldRefreshToken)).willReturn(memberId.toString());
        given(jwtTokenProvider.generateAccessToken(memberId)).willReturn("accessToken");
        given(jwtTokenProvider.generateRefreshToken(memberId)).willReturn(newRefreshToken);
        given(jwtTokenProvider.getTokenExpireTime(newRefreshToken)).willReturn(LocalDateTime.now().plusMinutes(10));

        // when
        LoginResponse response = authService.refreshTokens(oldRefreshToken);

        // then
        // 기존 토큰은 삭제됨
        then(redisTemplate.hasKey(oldRefreshToken)).isFalse();
        // 새 토큰은 저장됨
        String redisValue = redisTemplate.opsForValue().get(newRefreshToken);
        then(redisValue).isEqualTo(memberId.toString());
    }

    @Test
    void refreshToken_재발급_시_기존_토큰이_블랙리스트에_등록된다() {
        // given
        String oldRefreshToken = "oldRefreshToken2";
        String newRefreshToken = "newRefreshToken2";
        Member member = memberService.save(new Member(300L, SocialType.KAKAO, "hyun2", "profile_image2"));
        Long memberId = member.getId();
        refreshTokenService.save(oldRefreshToken, memberId.toString(), LocalDateTime.now().plusMinutes(10));
        given(jwtTokenProvider.getSubjectFromToken(oldRefreshToken)).willReturn(memberId.toString());
        given(jwtTokenProvider.generateAccessToken(memberId)).willReturn("accessToken2");
        given(jwtTokenProvider.generateRefreshToken(memberId)).willReturn(newRefreshToken);
        given(jwtTokenProvider.getTokenExpireTime(newRefreshToken)).willReturn(LocalDateTime.now().plusMinutes(10));
        given(jwtTokenProvider.getTokenExpireTime(oldRefreshToken)).willReturn(LocalDateTime.now().plusMinutes(10));

        // when
        authService.refreshTokens(oldRefreshToken);

        // then
        then(refreshTokenService.isBlacklisted(oldRefreshToken)).isTrue();
    }

    @Test
    void 블랙리스트에_등록된_refreshToken으로_재발급_요청시_예외가_발생한다() {
        // given
        String blacklistedToken = "blacklistedToken";
        Long memberId = 400L;
        refreshTokenService.save(blacklistedToken, memberId.toString(), LocalDateTime.now().plusMinutes(10));
        given(jwtTokenProvider.getSubjectFromToken(blacklistedToken)).willReturn(memberId.toString());
        given(jwtTokenProvider.getTokenExpireTime(blacklistedToken)).willReturn(LocalDateTime.now().plusMinutes(10));
        // 블랙리스트 등록
        refreshTokenService.addToBlacklist(blacklistedToken, 10 * 60 * 1000);

        // when & then
        Assertions.assertThrows(InvalidTokenException.class, () -> {
            authService.refreshTokens(blacklistedToken);
        });
    }

    @Test
    void 로그아웃_시_refreshToken이_블랙리스트에_등록된다() {
        // given
        String refreshToken = "logoutToken2";
        Long memberId = 500L;
        refreshTokenService.save(refreshToken, memberId.toString(), LocalDateTime.now().plusMinutes(10));
        given(jwtTokenProvider.getTokenExpireTime(refreshToken)).willReturn(LocalDateTime.now().plusMinutes(10));

        // when
        authService.logout(refreshToken);

        // then
        then(refreshTokenService.isBlacklisted(refreshToken)).isTrue();
    }

    @Test
    void 로그아웃_시_refreshToken이_Redis에서_삭제된다() {
        // given
        String refreshToken = "logoutToken";
        Long memberId = 200L;
        refreshTokenService.save(refreshToken, memberId.toString(), LocalDateTime.now().plusMinutes(10));

        // when
        authService.logout(refreshToken);

        // then
        then(redisTemplate.hasKey(refreshToken)).isFalse();
    }
}
