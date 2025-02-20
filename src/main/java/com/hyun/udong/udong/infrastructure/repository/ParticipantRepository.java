package com.hyun.udong.udong.infrastructure.repository;

import com.hyun.udong.udong.domain.Participant;
import com.hyun.udong.udong.domain.Udong;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Long>, ParticipantRepositoryCustom {
    boolean existsByUdongAndMemberId(Udong udongId, Long memberId);
}
