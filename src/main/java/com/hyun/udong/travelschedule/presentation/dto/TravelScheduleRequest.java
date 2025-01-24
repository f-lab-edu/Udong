package com.hyun.udong.travelschedule.presentation.dto;

import com.hyun.udong.common.annotation.DateFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TravelScheduleRequest {

    @NotNull
    @DateFormat
    private LocalDate startDate;

    @NotNull
    @DateFormat
    private LocalDate endDate;

    @NotNull
    private List<Long> cityIds;

}
