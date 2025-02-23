package com.hyun.udong.udong.infrastructure.repository.participant;

import com.hyun.udong.udong.presentation.dto.response.ParticipantCountResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.hyun.udong.udong.domain.QParticipant.participant;

@RequiredArgsConstructor
public class ParticipantRepositoryImpl implements ParticipantRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ParticipantCountResponse> countParticipantsByUdongIds(List<Long> udongIds) {
        return queryFactory
                .select(Projections.constructor(ParticipantCountResponse.class,
                        participant.udong.id,
                        participant.count()))
                .from(participant)
                .where(participant.udong.id.in(udongIds))
                .groupBy(participant.udong.id)
                .fetch();
    }
}
