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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
        BooleanExpression condition = Expressions.TRUE;

        if (filter.getCities() != null && !filter.getCities().isEmpty()) {
            condition = condition.and(cityIn(filter.getCities()));
        }
        if (filter.getStartDate() != null) {
            condition = condition.and(startDateGoe(filter.getStartDate()));
        }
        if (filter.getEndDate() != null) {
            condition = condition.and(endDateLoe(filter.getEndDate()));
        }
        if (filter.getTags() != null && !filter.getTags().isEmpty()) {
            condition = condition.and(tagsIn(filter.getTags()));
        }

        return condition;
    }

    private BooleanExpression cityIn(List<Long> cities) {
        return (cities != null && !cities.isEmpty()) ? travelCity.city.id.in(cities) : null;
    }

    private BooleanExpression startDateGoe(LocalDate startDate) {
        return startDate != null ? udong.travelPlanner.startDate.goe(startDate) : null;
    }

    private BooleanExpression endDateLoe(LocalDate endDate) {
        return endDate != null ? udong.travelPlanner.endDate.loe(endDate) : null;
    }

    private BooleanExpression tagsIn(List<String> tags) {
        return (tags != null && !tags.isEmpty()) ? udong.attachedTags.tags.any().in(tags) : null;
    }
}
