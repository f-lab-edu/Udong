package com.hyun.udong.auth.presentation.controller;

import com.hyun.udong.auth.application.service.RefreshTokenService;
import com.hyun.udong.auth.infrastructure.client.KakaoOAuthClient;
import com.hyun.udong.auth.presentation.dto.KakaoProfileResponse;
import com.hyun.udong.auth.presentation.dto.KakaoTokenResponse;
import com.hyun.udong.auth.util.JwtTokenProvider;
import com.hyun.udong.common.util.DataCleanerExtension;
import com.hyun.udong.member.application.service.MemberService;
import com.hyun.udong.member.domain.Member;
import com.hyun.udong.member.domain.SocialType;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(DataCleanerExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTest {


    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private KakaoOAuthClient kakaoOAuthClient;

    @Autowired
    private MemberService memberService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void 카카오_로그인_성공_시_loginResponse가_반환되는지_확인한다() {
        // given
        KakaoTokenResponse kakaoTokenResponse = new KakaoTokenResponse("accessToken");
        KakaoProfileResponse kakaoProfileResponse = new KakaoProfileResponse(100L, "hyun", "profile_image");
        given(kakaoOAuthClient.getToken(anyString())).willReturn(kakaoTokenResponse);
        given(kakaoOAuthClient.getUserProfile(anyString())).willReturn(kakaoProfileResponse);

        // when & then
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .param("code", "authCode")

                .when()
                .get("/api/auth/oauth/kakao")

                .then().log().all()
                .statusCode(200)
                .body("nickname", equalTo(kakaoProfileResponse.getKakaoAccount().getNickname()))
                .body("token.accessToken", notNullValue())
                .body("token.refreshToken", notNullValue());
    }

    @Test
    void 토큰_갱신_성공_시_loginResponse가_반환되는지_확인한다() {
        // given
        Member member = memberService.save(new Member(100L, SocialType.KAKAO, "hyun", "profile_image"));
        String refreshToken = jwtTokenProvider.generateRefreshToken(member.getId());

        // when & then
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .param("refreshToken", refreshToken)

                .when()
                .get("/api/auth/token/refresh")

                .then().log().all()
                .statusCode(200)
                .body("id", equalTo(member.getId().intValue()))
                .body("nickname", equalTo(member.getNickname()))
                .body("token.accessToken", notNullValue())
                .body("token.refreshToken", notNullValue());
    }

    @Test
    void 로그아웃_요청시_204와_블랙리스트_등록이_정상적으로_동작한다() {
        // given
        Long memberId = 12345L;
        String refreshToken = jwtTokenProvider.generateRefreshToken(memberId);
        refreshTokenService.save(refreshToken, memberId.toString(), jwtTokenProvider.getTokenExpireTime(refreshToken));

        // when & then
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .queryParam("refreshToken", refreshToken)
                .when()
                .post("/api/auth/logout")
                .then().log().all()
                .statusCode(204);
    }
}
