package com.hyun.udong.udong.infrastructure.repository;

import com.hyun.udong.udong.domain.Udong;
import com.hyun.udong.udong.presentation.dto.request.FindUdongsCondition;
import com.hyun.udong.udong.presentation.dto.response.ParticipantCountResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UdongRepositoryCustom {
    Page<Udong> findByFilter(FindUdongsCondition request, Pageable pageable);

    List<ParticipantCountResponse> countParticipantsByUdongIds(List<Long> udongIds);
}
