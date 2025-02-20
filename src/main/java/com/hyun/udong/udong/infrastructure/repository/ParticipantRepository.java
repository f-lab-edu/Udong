package com.hyun.udong.udong.infrastructure.repository;

import com.hyun.udong.udong.domain.Participant;
import com.hyun.udong.udong.domain.Udong;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Long>, ParticipantRepositoryCustom {
    boolean existsByUdongAndMemberId(Udong udong, Long memberId);

    List<Participant> findByUdong(Udong udong);
}
