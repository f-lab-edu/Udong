package com.hyun.udong.member.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hyun.udong.member.domain.Member;
import com.hyun.udong.member.domain.SocialType;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findBySocialIdAndSocialType(Long socialId, SocialType socialType);
}

