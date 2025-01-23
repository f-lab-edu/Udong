package com.hyun.udong.travelschedule.presentation.dto;

import com.hyun.udong.travelschedule.domain.MemberTravelSchedule;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class TravelScheduleResponse {

    private LocalDate startDate;
    private LocalDate endDate;
    private List<TravelScheduleCityResponse> travelScheduleCities;

    @Getter
    @Builder
    public static class TravelScheduleCityResponse {
        private Long cityId;
        private String cityName;
    }

    public static TravelScheduleResponse from(MemberTravelSchedule travelSchedule) {
        return TravelScheduleResponse.builder()
                .startDate(travelSchedule.getStartDate())
                .endDate(travelSchedule.getEndDate())
                .travelScheduleCities(travelSchedule.getTravelScheduleCities().stream()
                        .map(city -> TravelScheduleCityResponse.builder()
                                .cityId(city.getCity().getId())
                                .cityName(city.getCity().getName())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
