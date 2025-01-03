package com.hyun.udong.member.application.service;

import com.hyun.udong.member.domain.Member;
import com.hyun.udong.member.infrastructure.repository.MemberRepository;
import com.hyun.udong.member.presentation.dto.MemberResponse;
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
    public MemberResponse save(Member member) {
        Optional<Member> foundMember = memberRepository.findBySocialIdAndSocialType(member.getSocialId(), member.getSocialType());

        if (foundMember.isPresent()) {
            foundMember.get().update(member.getNickname(), member.getProfileImageUrl());

            return new MemberResponse(foundMember.get());
        }

        return new MemberResponse(memberRepository.save(member));
    }

    public MemberResponse getMemberById(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당하는 회원이 없습니다."));

        return new MemberResponse(member);
    }
}
