package com.hyun.udong.udong.infrastructure.repository.waitingmember;

import com.hyun.udong.udong.domain.Udong;
import com.hyun.udong.udong.domain.WaitingMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WaitingMemberRepository extends JpaRepository<WaitingMember, Long> {
    boolean existsByUdongAndMemberId(Udong udong, Long memberId);

    Optional<WaitingMember> findByUdongAndMemberId(Udong udong, Long memberId);

    int countByUdong(Udong udong);

}
