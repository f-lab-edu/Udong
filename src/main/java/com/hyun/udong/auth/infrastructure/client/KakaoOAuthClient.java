package com.hyun.udong.auth.infrastructure.client;

import com.hyun.udong.auth.presentation.dto.KakaoProfileResponse;
import com.hyun.udong.auth.presentation.dto.KakaoTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
public class KakaoOAuthClient {

    private static final String ACCESS_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private static final String USER_PROFILE_URL = "https://kapi.kakao.com/v2/user/me";

    @Value("${social.kakao.client-id}")
    private String clientId;

    @Value("${social.kakao.redirect-uri}")
    private String redirectUri;

    private final RestClient restClient;

    public KakaoOAuthClient() {
        restClient = RestClient.builder()
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public KakaoProfileResponse getUserProfile(String accessToken) {
        return restClient.get()
                .uri(USER_PROFILE_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .body(KakaoProfileResponse.class);
    }

    public KakaoTokenResponse getToken(String code) {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("client_id", clientId);
        requestBody.add("redirect_uri", redirectUri);
        requestBody.add("code", code);

        return restClient.post()
                .uri(ACCESS_TOKEN_URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(requestBody)
                .retrieve()
                .body(KakaoTokenResponse.class);
    }
}
