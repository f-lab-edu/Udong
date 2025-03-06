package com.hyun.udong.udong.infrastructure.repository;

import com.hyun.udong.udong.domain.Udong;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UdongRepository extends JpaRepository<Udong, Long>, UdongRepositoryCustom {

    @Lock(LockModeType.OPTIMISTIC)
    @Query("select u from Udong u where u.id = :udongId")
    Optional<Udong> findUdongByWithOptimisticLock(@Param("udongId") Long udongId);
}
