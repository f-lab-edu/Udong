package com.hyun.udong.auth.application.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final KakaoOAuthClient kakaoOAuthClient;
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public LoginResponse kakaoLogin(String code) {
        KakaoTokenResponse kakaoTokenResponse = kakaoOAuthClient.getToken(code);
        KakaoProfileResponse profile = kakaoOAuthClient.getUserProfile(kakaoTokenResponse.getAccessToken());

        Member member = memberService.findBySocialIdAndSocialType(profile.getId(), SocialType.KAKAO)
                .orElseGet(() -> memberService.save(profile.toMember()));

        String accessToken = jwtTokenProvider.generateAccessToken(member.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(member.getId());
        updateRefreshToken(member.getId(), refreshToken);

        AuthTokens authTokens = new AuthTokens(accessToken, jwtTokenProvider.getTokenExpireTime(accessToken), refreshToken, jwtTokenProvider.getTokenExpireTime(refreshToken));
        return new LoginResponse(member.getId(), member.getNickname(), authTokens);
    }

    @Transactional
    public LoginResponse refreshTokens(String refreshToken) {
        Long memberId = extractMemberId(refreshToken);

        validateIsTokenOwner(refreshToken, memberId);

        String newAccessToken = jwtTokenProvider.generateAccessToken(memberId);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(memberId);

        Member member = updateRefreshToken(memberId, newRefreshToken);

        AuthTokens authTokens = new AuthTokens(newAccessToken, jwtTokenProvider.getTokenExpireTime(newAccessToken), newRefreshToken, jwtTokenProvider.getTokenExpireTime(newRefreshToken));
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

    private void validateIsTokenOwner(String refreshToken, Long memberId) {
        Long ownerId = memberRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> InvalidTokenException.EXCEPTION)
                .getId();

        if (!ownerId.equals(memberId)) {
            throw InvalidTokenException.EXCEPTION;
        }
    }

    private Member updateRefreshToken(Long memberId, String refreshToken) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("해당하는 회원이 존재하지 않습니다."));
        member.updateRefreshToken(refreshToken);
        return member;
    }

    public Member findMemberFromToken(String token) {
        Long memberId = Long.parseLong(jwtTokenProvider.getSubjectFromToken(token));
        return memberRepository.findById(memberId)
                .orElseThrow(() -> InvalidTokenException.EXCEPTION);
    }
}
