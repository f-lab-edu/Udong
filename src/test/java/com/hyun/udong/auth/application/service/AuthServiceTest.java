package com.hyun.udong.auth.application.service;

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

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;

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

    @Test
    @DisplayName("카카오 로그인 시 사용자 정보와 refresh_token이 저장된다.")
    void kakaoLogin_save() {
        // given
        String code = "authCode";
        KakaoTokenResponse kakaoTokenResponse = new KakaoTokenResponse("accessToken");
        KakaoProfileResponse profile = new KakaoProfileResponse(100L, "hyun", "profile_image");
        Member member = new Member(1L, 100L, SocialType.KAKAO, "hyun", "profile_image");

        given(kakaoOAuthClient.getToken(code)).willReturn(kakaoTokenResponse);
        given(kakaoOAuthClient.getUserProfile(kakaoTokenResponse.getAccessToken())).willReturn(profile);
        given(jwtTokenProvider.generateAccessToken(member.getId())).willReturn("accessToken");
        given(jwtTokenProvider.generateRefreshToken(member.getId())).willReturn("refreshToken");

        // when
        LoginResponse response = authService.kakaoLogin(code);

        // then
        then(response).isNotNull();
        then(response.getNickname()).isEqualTo(member.getNickname());
        then(response.getToken().accessToken()).isEqualTo("accessToken");
        then(response.getToken().refreshToken()).isEqualTo("refreshToken");
    }

    @Test
    @DisplayName("refreshToken 재발급 시 새로운 토큰을 저장한다.")
    void refreshTokens_returnsNewTokens() {
        // given
        String refreshToken = "validRefreshToken";
        Long memberId = 1L;
        Member member = new Member(100L, SocialType.KAKAO, "hyun", "profile_image");
        member.updateRefreshToken(refreshToken);
        memberService.save(member);

        given(jwtTokenProvider.getSubjectFromToken(refreshToken)).willReturn(memberId.toString());
        given(jwtTokenProvider.generateAccessToken(memberId)).willReturn("newAccessToken");
        given(jwtTokenProvider.generateRefreshToken(memberId)).willReturn("newRefreshToken");

        // when
        authService.refreshTokens(refreshToken);
        Member updatedMember = memberService.findById(member.getId());

        // then
        then(updatedMember.getRefreshToken()).isEqualTo("newRefreshToken");
    }
}
