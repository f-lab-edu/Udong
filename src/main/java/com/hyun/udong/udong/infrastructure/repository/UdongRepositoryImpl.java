package com.hyun.udong.udong.infrastructure.repository;

import com.hyun.udong.udong.domain.Udong;
import com.hyun.udong.udong.presentation.dto.request.FindUdongsCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.hyun.udong.common.util.querydsl.QueryDslUtils.*;
import static com.hyun.udong.udong.domain.QTravelCity.travelCity;
import static com.hyun.udong.udong.domain.QUdong.udong;

@RequiredArgsConstructor
public class UdongRepositoryImpl implements UdongRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Udong> findByFilter(FindUdongsCondition filter, Pageable pageable) {
        List<Udong> udongs = queryFactory
                .selectFrom(udong)
                .leftJoin(udong.travelCities, travelCity)
                .fetchJoin()
                .where(createWhere(filter))
                .orderBy(udong.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = Optional.ofNullable(queryFactory
                .select(udong.count())
                .from(udong)
                .leftJoin(udong.travelCities, travelCity)
                .where(createWhere(filter))
                .fetchOne()).orElse(0L);

        return new PageImpl<>(udongs, pageable, total);
    }

    private BooleanExpression createWhere(FindUdongsCondition filter) {
        return Expressions.TRUE
                .and(isNotEmptyAndIn(filter.getCities(), travelCity.city.id))
                .and(isNotNullAndGoe(filter.getStartDate(), udong.travelPlanner.startDate))
                .and(isNotNullAndLoe(filter.getEndDate(), udong.travelPlanner.endDate))
                .and(isNotEmptyAndIn(filter.getTags(), udong.attachedTags.tags.any()));
    }
}
