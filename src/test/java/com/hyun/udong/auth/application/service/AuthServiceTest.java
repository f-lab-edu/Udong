package com.hyun.udong.auth.application.service;

import com.hyun.udong.auth.exception.InvalidTokenException;
import com.hyun.udong.auth.infrastructure.client.KakaoOAuthClient;
import com.hyun.udong.auth.presentation.dto.KakaoProfileResponse;
import com.hyun.udong.auth.presentation.dto.KakaoTokenResponse;
import com.hyun.udong.auth.presentation.dto.LoginResponse;
import com.hyun.udong.auth.util.JwtTokenProvider;
import com.hyun.udong.member.application.service.MemberService;
import com.hyun.udong.member.domain.Member;
import com.hyun.udong.member.domain.SocialType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Date;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class AuthServiceTest {

    @MockitoBean
    private KakaoOAuthClient kakaoOAuthClient;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MemberService memberService;

    @Autowired
    private AuthService authService;

    @Test
    @DisplayName("카카오 로그인 시 사용자 정보와 refresh_token이 저장된다.")
    void kakaoLogin_save() {
        // given
        String code = "authCode";
        KakaoTokenResponse kakaoTokenResponse = new KakaoTokenResponse("accessToken");
        KakaoProfileResponse profile = new KakaoProfileResponse(100L, "hyun", "profile_image");

        given(kakaoOAuthClient.getToken(code)).willReturn(kakaoTokenResponse);
        given(kakaoOAuthClient.getUserProfile(kakaoTokenResponse.getAccessToken())).willReturn(profile);

        // when
        LoginResponse response = authService.kakaoLogin(code);

        // then
        Member member = memberService.findBySocialIdAndSocialType(profile.getId(), SocialType.KAKAO).orElseThrow();
        then(response).isNotNull();
        then(response.getId()).isEqualTo(member.getId());
        then(response.getId()).isEqualTo(Long.parseLong(jwtTokenProvider.getSubjectFromToken(response.getToken().accessToken())));
        then(response.getId()).isEqualTo(Long.parseLong(jwtTokenProvider.getSubjectFromToken(response.getToken().refreshToken())));
        then(response.getToken().refreshToken()).isEqualTo(member.getRefreshToken());
    }

    @Test
    @DisplayName("refreshToken 재발급 시 새로운 토큰을 저장한다.")
    void refreshTokens_ok() {
        // given
        Member member = memberService.save(new Member(100L, SocialType.KAKAO, "hyun", "profile_image"));
        String initialRefreshToken = jwtTokenProvider.generateRefreshToken(member.getId(), new Date(System.currentTimeMillis() - 1000));

        member.updateRefreshToken(initialRefreshToken);
        memberService.save(member);

        // when
        authService.refreshTokens(initialRefreshToken);

        // then
        Member updatedMember = memberService.findById(member.getId());
        then(updatedMember.getRefreshToken()).isNotEqualTo(initialRefreshToken);
    }


    @DisplayName("토큰 재발급 시 유효한 코드가 아니면 예외가 발생한다.")
    @Test
    void refreshTokens_throw() {
        String otherRefreshToken = jwtTokenProvider.generateRefreshToken(200L, new Date());

        // when & then
        thenThrownBy(() -> authService.refreshTokens(otherRefreshToken))
                .isInstanceOf(InvalidTokenException.class);
    }
}
