package com.hyun.udong.auth.presentation.controller;

import com.hyun.udong.auth.infrastructure.client.KakaoOAuthClient;
import com.hyun.udong.auth.presentation.dto.KakaoProfileResponse;
import com.hyun.udong.auth.presentation.dto.KakaoTokenResponse;
import com.hyun.udong.auth.util.JwtTokenProvider;
import com.hyun.udong.member.application.service.MemberService;
import com.hyun.udong.member.domain.Member;
import com.hyun.udong.member.domain.SocialType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuthControllerTest {

    public static final long FIRST_SAVED_MEMBER_ID = 1L;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private KakaoOAuthClient kakaoOAuthClient;

    @Autowired
    private MemberService memberService;

    @DisplayName("카카오 로그인 성공 시 loginResponse가 반환되는지 확인한다.")
    @Test
    void kakaoLogin() throws Exception {
        // given
        KakaoTokenResponse kakaoTokenResponse = new KakaoTokenResponse("accessToken");
        KakaoProfileResponse kakaoProfileResponse = new KakaoProfileResponse(100L, "hyun", "profile_image");
        given(kakaoOAuthClient.getToken(anyString())).willReturn(kakaoTokenResponse);
        given(kakaoOAuthClient.getUserProfile(anyString())).willReturn(kakaoProfileResponse);

        // when & then
        mockMvc.perform(get("/auth/oauth/kakao")
                        .param("code", "authCode"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(FIRST_SAVED_MEMBER_ID))
                .andExpect(jsonPath("$.nickname").value(kakaoProfileResponse.getKakaoAccount().getNickname()))
                .andExpect(jsonPath("$.token.accessToken").exists())
                .andExpect(jsonPath("$.token.refreshToken").exists());
    }

    @DisplayName("토큰 갱신 성공 시 loginResponse가 반환되는지 확인한다.")
    @Test
    void refreshTokens() throws Exception {
        // given
        Member member = memberService.save(new Member(100L, SocialType.KAKAO, "hyun", "profile_image"));
        String refreshToken = jwtTokenProvider.generateRefreshToken(member.getId());

        member.updateRefreshToken(refreshToken);
        memberService.save(member);

        // when & then
        mockMvc.perform(get("/auth/token/refresh")
                        .param("refreshToken", refreshToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(member.getId()))
                .andExpect(jsonPath("$.nickname").value(member.getNickname()))
                .andExpect(jsonPath("$.token.accessToken").exists())
                .andExpect(jsonPath("$.token.refreshToken").exists());

    }
}
