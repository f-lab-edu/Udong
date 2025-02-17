package com.hyun.udong.udong.infrastructure.repository;

import com.hyun.udong.udong.domain.QParticipant;
import com.hyun.udong.udong.presentation.dto.response.ParticipantCountResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ParticipantRepositoryImpl implements ParticipantRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ParticipantCountResponse> countParticipantsByUdongIds(List<Long> udongIds) {
        QParticipant participant = QParticipant.participant;

        return queryFactory
                .select(Projections.constructor(ParticipantCountResponse.class,
                        participant.udong.id,
                        participant.count().intValue()))
                .from(participant)
                .where(participant.udong.id.in(udongIds))
                .groupBy(participant.udong.id)
                .fetch();
    }
}
