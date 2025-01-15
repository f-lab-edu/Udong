package com.hyun.udong.member.application.service;

import com.hyun.udong.member.domain.Member;
import com.hyun.udong.member.domain.SocialType;
import com.hyun.udong.member.exception.MemberNotFoundException;
import com.hyun.udong.member.infrastructure.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public Member save(Member member) {
        return memberRepository.save(member);
    }

    public Optional<Member> findBySocialIdAndSocialType(Long socialId, SocialType socialType) {
        return memberRepository.findBySocialIdAndSocialType(socialId, socialType);
    }

    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> MemberNotFoundException.EXCEPTION);
    }

    public Member updateRefreshToken(Long id, String refreshToken) {
        Member member = findById(id);
        member.updateRefreshToken(refreshToken);
        return member;
    }
}
