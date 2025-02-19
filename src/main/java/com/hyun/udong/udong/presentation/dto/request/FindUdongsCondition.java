package com.hyun.udong.udong.presentation.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FindUdongsCondition {

    private Long country;

    private List<Long> cities;

    @FutureOrPresent(message = "시작일은 현재 또는 미래 날짜여야 합니다.")
    private LocalDate startDate;

    @Future(message = "종료일은 미래 날짜여야 합니다.")
    private LocalDate endDate;

    private List<String> tags;
}
