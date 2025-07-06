package com.hyun.udong.auth.application.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hyun.udong.auth.exception.InvalidTokenException;
import com.hyun.udong.auth.infrastructure.client.KakaoOAuthClient;
import com.hyun.udong.auth.presentation.dto.AuthTokens;
import com.hyun.udong.auth.presentation.dto.KakaoProfileResponse;
import com.hyun.udong.auth.presentation.dto.KakaoTokenResponse;
import com.hyun.udong.auth.presentation.dto.LoginResponse;
import com.hyun.udong.auth.util.JwtTokenProvider;
import com.hyun.udong.common.exception.NotFoundException;
import com.hyun.udong.member.application.service.MemberService;
import com.hyun.udong.member.domain.Member;
import com.hyun.udong.member.domain.SocialType;
import com.hyun.udong.member.infrastructure.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final KakaoOAuthClient kakaoOAuthClient;
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public LoginResponse kakaoLogin(String code) {
        KakaoTokenResponse kakaoTokenResponse = kakaoOAuthClient.getToken(code);
        KakaoProfileResponse profile = kakaoOAuthClient.getUserProfile(kakaoTokenResponse.getAccessToken());

        Member member = memberService.findBySocialIdAndSocialType(profile.getId(), SocialType.KAKAO)
                .orElseGet(() -> memberService.save(profile.toMember()));

        String accessToken = jwtTokenProvider.generateAccessToken(member.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(member.getId());
        LocalDateTime refreshTokenExpireTime = jwtTokenProvider.getTokenExpireTime(refreshToken);
        refreshTokenService.save(refreshToken, member.getId().toString(), refreshTokenExpireTime);

        AuthTokens authTokens = new AuthTokens(accessToken, jwtTokenProvider.getTokenExpireTime(accessToken), refreshToken, jwtTokenProvider.getTokenExpireTime(refreshToken));
        return new LoginResponse(member.getId(), member.getNickname(), authTokens);
    }

    @Transactional
    public LoginResponse refreshTokens(String refreshToken) {
        if (!refreshTokenService.isValid(refreshToken)) {
            throw InvalidTokenException.EXCEPTION;
        }
        Long memberId = extractMemberId(refreshToken);

        // 기존 토큰 삭제
        refreshTokenService.delete(refreshToken);

        // 새 토큰 발급 및 저장
        String newAccessToken = jwtTokenProvider.generateAccessToken(memberId);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(memberId);
        LocalDateTime newRefreshTokenExpireTime = jwtTokenProvider.getTokenExpireTime(newRefreshToken);
        refreshTokenService.save(newRefreshToken, memberId.toString(), newRefreshTokenExpireTime);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("해당하는 회원이 존재하지 않습니다."));

        AuthTokens authTokens = new AuthTokens(newAccessToken, jwtTokenProvider.getTokenExpireTime(newAccessToken), newRefreshToken, newRefreshTokenExpireTime);
        return new LoginResponse(member.getId(), member.getNickname(), authTokens);
    }

    private long extractMemberId(String refreshToken) {
        long memberId;
        try {
            memberId = Long.parseLong(jwtTokenProvider.getSubjectFromToken(refreshToken));
        } catch (NumberFormatException e) {
            throw InvalidTokenException.EXCEPTION;
        }
        return memberId;
    }

    public Member findMemberFromToken(String token) {
        Long memberId = Long.parseLong(jwtTokenProvider.getSubjectFromToken(token));
        return memberRepository.findById(memberId)
                .orElseThrow(() -> InvalidTokenException.EXCEPTION);
    }

    public void logout(String refreshToken) {
        refreshTokenService.delete(refreshToken);
    }
}
