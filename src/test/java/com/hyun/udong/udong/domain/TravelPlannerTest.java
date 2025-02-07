package com.hyun.udong.udong.domain;

import com.hyun.udong.common.exception.InvalidParameterException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class TravelPlannerTest {

    @Test
    void 시작일과_종료일이_null이면_예외발생한다() {
        assertThatThrownBy(() -> TravelPlanner.of(null, null))
                .isInstanceOf(InvalidParameterException.class)
                .hasMessage("여행 시작일과 종료일을 모두 입력해야 합니다.");
    }


    @Test
    void 시작일이_오늘보다_과거면_예외발생한다() {
        LocalDate pastDate = LocalDate.now().minusDays(1);

        assertThatThrownBy(() -> TravelPlanner.of(pastDate, LocalDate.now().plusDays(5)))
                .isInstanceOf(InvalidParameterException.class)
                .hasMessage("여행 시작일은 오늘 이후여야 합니다.");
    }

    @Test
    void 종료일이_시작일보다_이전이면_예외발생한다() {
        LocalDate startDate = LocalDate.now().plusDays(5);
        LocalDate endDate = LocalDate.now().plusDays(2);

        assertThatThrownBy(() -> TravelPlanner.of(startDate, endDate))
                .isInstanceOf(InvalidParameterException.class)
                .hasMessage("여행 종료일은 시작일 이후여야 합니다.");
    }

    @Test
    void 정상적인_날짜_생성() {
        LocalDate startDate = LocalDate.now().plusDays(5);
        LocalDate endDate = LocalDate.now().plusDays(10);
        TravelPlanner travelPlanner = TravelPlanner.of(startDate, endDate);

        assertThat(travelPlanner.getStartDate()).isEqualTo(startDate);
        assertThat(travelPlanner.getEndDate()).isEqualTo(endDate);
    }
}
