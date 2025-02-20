package com.hyun.udong.udong.infrastructure.repository;

import com.hyun.udong.udong.domain.Udong;
import com.hyun.udong.udong.domain.WaitingMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WaitingMemberRepository extends JpaRepository<WaitingMember, Long> {
    boolean existsByUdongAndMemberId(Udong udong, Long memberId);
}
