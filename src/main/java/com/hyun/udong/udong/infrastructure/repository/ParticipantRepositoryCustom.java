package com.hyun.udong.udong.infrastructure.repository;

import com.hyun.udong.udong.presentation.dto.response.ParticipantCountResponse;

import java.util.List;

public interface ParticipantRepositoryCustom {
    List<ParticipantCountResponse> countParticipantsByUdongIds(List<Long> udongIds);
}
