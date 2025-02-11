package com.hyun.udong.udong.infrastructure.repository;

import com.hyun.udong.udong.domain.Udong;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UdongRepository extends JpaRepository<Udong, Long> {
}
