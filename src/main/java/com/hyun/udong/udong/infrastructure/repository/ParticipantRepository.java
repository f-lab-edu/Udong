package com.hyun.udong.udong.infrastructure.repository;

import com.hyun.udong.udong.domain.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    List<Participant> findAllByUdongId(Long udongId);
}
