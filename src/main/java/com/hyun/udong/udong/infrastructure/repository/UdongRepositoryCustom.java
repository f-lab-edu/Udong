package com.hyun.udong.udong.infrastructure.repository;

import com.hyun.udong.udong.domain.Udong;
import com.hyun.udong.udong.presentation.dto.FindUdongsCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UdongRepositoryCustom {
    Page<Udong> findByFilter(FindUdongsCondition request, Pageable pageable);
}
