package com.hyun.udong.auth.application.service;

import com.hyun.udong.auth.infrastructure.client.KakaoOAuthClient;
import com.hyun.udong.auth.presentation.dto.AccessTokenResponse;
import com.hyun.udong.auth.presentation.dto.KakaoProfileResponse;
import com.hyun.udong.auth.presentation.dto.KakaoTokenResponse;
import com.hyun.udong.member.application.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoOAuthClient kakaoOAuthClient;
    private final MemberService memberService;

    public AccessTokenResponse kakaoLogin(String code) {
        KakaoTokenResponse kakaoTokenResponse = kakaoOAuthClient.getToken(code);
        KakaoProfileResponse profile = kakaoOAuthClient.getUserProfile(kakaoTokenResponse.getAccessToken());
        memberService.save(profile.toMember());
        return new AccessTokenResponse(kakaoTokenResponse.getIdToken(), kakaoTokenResponse.getExpiresIn());
    }
}
