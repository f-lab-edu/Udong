package com.hyun.udong.auth.presentation.controller;

import com.hyun.udong.auth.infrastructure.client.KakaoOAuthClient;
import com.hyun.udong.auth.presentation.dto.AccessTokenResponse;
import com.hyun.udong.auth.presentation.dto.KakaoProfileResponse;
import com.hyun.udong.auth.presentation.dto.KakaoTokenResponse;
import com.hyun.udong.member.application.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final KakaoOAuthClient kakaoOAuthClient;
    private final MemberService memberService;

    @GetMapping("/login/kakao")
    public ResponseEntity<AccessTokenResponse> kakaoLogin(@RequestParam("code") final String code) {
        KakaoTokenResponse kakaoTokenResponse = kakaoOAuthClient.getToken(code);
        KakaoProfileResponse profile = kakaoOAuthClient.getUserProfile(kakaoTokenResponse.getAccessToken());
        memberService.saveOrUpdate(profile.toMember());
        return ResponseEntity.ok().body(new AccessTokenResponse(kakaoTokenResponse.getIdToken(), kakaoTokenResponse.getExpiresIn()));
    }
}
