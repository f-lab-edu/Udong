package com.hyun.udong.auth.presentation.controller;

import com.hyun.udong.auth.infrastructure.client.KakaoOAuthClient;
import com.hyun.udong.auth.presentation.dto.KakaoProfileResponse;
import com.hyun.udong.auth.presentation.dto.KakaoTokenResponse;
import com.hyun.udong.auth.util.JwtTokenProvider;
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

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private KakaoOAuthClient kakaoOAuthClient;

    @DisplayName("카카오 로그인 성공 시 loginResponse가 반환되는지 확인한다.")
    @Test
    void kakaoLogin() throws Exception {
        // given
        KakaoTokenResponse kakaoTokenResponse = new KakaoTokenResponse("accessToken");
        KakaoProfileResponse kakaoProfileResponse = new KakaoProfileResponse(100L, "hyun", "profile_image");
        given(kakaoOAuthClient.getToken(anyString())).willReturn(kakaoTokenResponse);
        given(kakaoOAuthClient.getUserProfile(anyString())).willReturn(kakaoProfileResponse);
        given(jwtTokenProvider.generateAccessToken(FIRST_SAVED_MEMBER_ID)).willReturn("accessToken");
        given(jwtTokenProvider.generateRefreshToken(FIRST_SAVED_MEMBER_ID)).willReturn("refreshToken");

        // when & then
        mockMvc.perform(get("/auth/oauth/kakao")
                        .param("code", "authCode"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(FIRST_SAVED_MEMBER_ID))
                .andExpect(jsonPath("$.nickname").value(kakaoProfileResponse.getKakaoAccount().getNickname()))
                .andExpect(jsonPath("$.token.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.token.refreshToken").value("refreshToken"));
    }
}
