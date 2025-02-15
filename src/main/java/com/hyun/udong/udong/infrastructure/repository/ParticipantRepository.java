package com.hyun.udong.udong.infrastructure.repository;

import com.hyun.udong.udong.domain.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    @Query("SELECT p.udong.id, COUNT(p) FROM Participant p WHERE p.udong.id IN :udongIds GROUP BY p.udong.id")
    List<Object[]> countParticipantsByUdongIds(@Param("udongIds") List<Long> udongIds);
}
