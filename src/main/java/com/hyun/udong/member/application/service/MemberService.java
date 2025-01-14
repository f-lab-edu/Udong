package com.hyun.udong.member.application.service;

import com.hyun.udong.member.domain.Member;
import com.hyun.udong.member.exception.MemberNotFoundException;
import com.hyun.udong.member.infrastructure.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public Member save(Member member) {
        return memberRepository.findBySocialIdAndSocialType(member.getSocialId(), member.getSocialType())
                .orElseGet(() -> memberRepository.save(member));
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
