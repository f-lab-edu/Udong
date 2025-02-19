package com.hyun.udong.common.util.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.core.types.dsl.SetPath;
import com.querydsl.core.types.dsl.SimpleExpression;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Optional;

@UtilityClass
public class QueryDslUtils {

    // @ElementCollection 또는 컬렉션 필드
    public static <T> BooleanExpression isNotEmptyAndIn(List<T> values, SetPath<T, SimpleExpression<T>> path) {
        return Optional.ofNullable(values)
                .filter(list -> !list.isEmpty())
                .map(value -> path.any().in(value)) // any(): 컬렉션 필드의 각 요소에 대해 조건을 적용
                .orElse(null);
    }

    // 일반 단일 컬럼
    public static <T> BooleanExpression isNotEmptyAndIn(List<T> values, SimpleExpression<T> path) {
        return Optional.ofNullable(values)
                .filter(list -> !list.isEmpty())
                .map(path::in) // in(): 컬럼에 대해 조건을 적용
                .orElse(null);
    }

    public static <T extends Comparable<?>> BooleanExpression isNotNullAndGoe(T value, ComparableExpression<T> path) {
        return Optional.ofNullable(value)
                .map(path::goe)
                .orElse(null);
    }

    public static <T extends Comparable<?>> BooleanExpression isNotNullAndLoe(T value, ComparableExpression<T> path) {
        return Optional.ofNullable(value)
                .map(path::loe)
                .orElse(null);
    }
}
