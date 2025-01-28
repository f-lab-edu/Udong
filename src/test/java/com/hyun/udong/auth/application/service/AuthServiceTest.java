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

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
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
    void 카카오_로그인_시_사용자_정보와_refresh_token이_저장된다() {
        // given
        String code = "authCode";
        KakaoTokenResponse kakaoTokenResponse = new KakaoTokenResponse("accessToken");
        KakaoProfileResponse profile = new KakaoProfileResponse(100L, "hyun", "profile_image");

        given(kakaoOAuthClient.getToken(code)).willReturn(kakaoTokenResponse);
        given(kakaoOAuthClient.getUserProfile(kakaoTokenResponse.getAccessToken())).willReturn(profile);
        given(jwtTokenProvider.generateAccessToken(1L)).willReturn("accessToken");
        given(jwtTokenProvider.generateRefreshToken(1L)).willReturn("refreshToken");

        // when
        LoginResponse response = authService.kakaoLogin(code);

        // then
        Member member = memberService.findBySocialIdAndSocialType(profile.getId(), SocialType.KAKAO).orElseThrow();
        then(response).isNotNull();
        then(response.getId()).isEqualTo(member.getId());
        then(response.getToken().accessToken()).isEqualTo("accessToken");
        then(response.getToken().refreshToken()).isEqualTo("refreshToken");
    }

    @Test
    @DisplayName(" 재발급 시 새로운 토큰을 저장한다.")
    void refreshToken_재발급_시_새로운_토큰을_저장한다() {
        // given
        Member member = memberService.save(new Member(100L, SocialType.KAKAO, "hyun", "profile_image"));
        String initialRefreshToken = "initialRefreshToken";
        member.updateRefreshToken(initialRefreshToken);
        memberService.save(member);

        String newRefreshToken = "newRefreshToken";
        given(jwtTokenProvider.generateRefreshToken(member.getId())).willReturn(newRefreshToken);
        given(jwtTokenProvider.getSubjectFromToken(initialRefreshToken)).willReturn(String.valueOf(member.getId()));

        // when
        authService.refreshTokens(initialRefreshToken);

        // then
        Member updatedMember = memberService.findById(member.getId());
        then(updatedMember.getRefreshToken()).isNotEqualTo(initialRefreshToken);
        then(updatedMember.getRefreshToken()).isEqualTo(newRefreshToken);
    }

    @Test
    void 토큰_재발급_시_유효한_코드가_아니면_예외가_발생한다() {
        String otherRefreshToken = jwtTokenProvider.generateRefreshToken(200L);

        // when & then
        thenThrownBy(() -> authService.refreshTokens(otherRefreshToken))
                .isInstanceOf(InvalidTokenException.class);
    }
}
