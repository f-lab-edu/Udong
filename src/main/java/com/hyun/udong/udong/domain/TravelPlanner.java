package com.hyun.udong.udong.domain;

import com.hyun.udong.common.exception.InvalidParameterException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static lombok.AccessLevel.PROTECTED;

@Embeddable
@Getter
@NoArgsConstructor(access = PROTECTED)
public class TravelPlanner {

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    private void validate(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new InvalidParameterException("여행 시작일과 종료일을 모두 입력해야 합니다.");
        }

        if (startDate.isBefore(LocalDate.now())) {
            throw new InvalidParameterException("여행 시작일은 오늘 이후여야 합니다.");
        }

        if (endDate.isBefore(startDate)) {
            throw new InvalidParameterException("여행 종료일은 시작일 이후여야 합니다.");
        }
    }

    private TravelPlanner(LocalDate startDate, LocalDate endDate) {
        validate(startDate, endDate);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static TravelPlanner of(LocalDate startDate, LocalDate endDate) {
        return new TravelPlanner(startDate, endDate);
    }

}
