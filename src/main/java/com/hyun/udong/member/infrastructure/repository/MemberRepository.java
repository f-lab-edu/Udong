package com.hyun.udong.member.infrastructure.repository;

import com.hyun.udong.member.domain.Member;
import com.hyun.udong.member.domain.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findBySocialIdAndSocialType(Long socialId, SocialType socialType);
}

