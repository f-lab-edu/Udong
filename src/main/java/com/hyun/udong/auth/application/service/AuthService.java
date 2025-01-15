package com.hyun.udong.auth.application.service;

import com.hyun.udong.auth.infrastructure.client.KakaoOAuthClient;
import com.hyun.udong.auth.presentation.dto.AuthTokens;
import com.hyun.udong.auth.presentation.dto.KakaoProfileResponse;
import com.hyun.udong.auth.presentation.dto.KakaoTokenResponse;
import com.hyun.udong.auth.presentation.dto.LoginResponse;
import com.hyun.udong.auth.util.JwtTokenProvider;
import com.hyun.udong.member.application.service.MemberService;
import com.hyun.udong.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final KakaoOAuthClient kakaoOAuthClient;
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public LoginResponse kakaoLogin(String code) {
        KakaoTokenResponse kakaoTokenResponse = kakaoOAuthClient.getToken(code);
        KakaoProfileResponse profile = kakaoOAuthClient.getUserProfile(kakaoTokenResponse.getAccessToken());

        Member member = memberService.save(profile.toMember());

        String accessToken = jwtTokenProvider.generateAccessToken(member.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(member.getId());
        memberService.updateRefreshToken(member.getId(), refreshToken);

        AuthTokens authTokens = new AuthTokens(accessToken, jwtTokenProvider.getTokenExpireTime(accessToken), refreshToken, jwtTokenProvider.getTokenExpireTime(refreshToken));
        return new LoginResponse(member.getId(), member.getNickname(), authTokens);
    }

    @Transactional
    public LoginResponse refreshTokens(String refreshToken) {
        Long memberId = Long.parseLong(jwtTokenProvider.getSubjectFromToken(refreshToken));
        String newAccessToken = jwtTokenProvider.generateAccessToken(memberId);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(memberId);

        Member member = memberService.updateRefreshToken(memberId, newRefreshToken);

        AuthTokens authTokens = new AuthTokens(newAccessToken, jwtTokenProvider.getTokenExpireTime(newAccessToken), newRefreshToken, jwtTokenProvider.getTokenExpireTime(newRefreshToken));
        return new LoginResponse(member.getId(), member.getNickname(), authTokens);

    }
}
