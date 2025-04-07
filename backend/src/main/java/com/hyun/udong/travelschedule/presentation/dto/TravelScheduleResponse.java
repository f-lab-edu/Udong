package com.hyun.udong.travelschedule.presentation.dto;

import com.hyun.udong.common.annotation.DateFormat;
import com.hyun.udong.travelschedule.domain.TravelSchedule;
import com.hyun.udong.travelschedule.domain.TravelScheduleCity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class TravelScheduleResponse {

    @DateFormat
    private LocalDate startDate;

    @DateFormat
    private LocalDate endDate;

    private List<TravelScheduleCityResponse> travelScheduleCities;

    @Getter
    @Builder
    public static class TravelScheduleCityResponse {
        private Long cityId;
        private String cityName;
        private String countryName;

        public static TravelScheduleCityResponse from(TravelScheduleCity travelScheduleCity) {
            return TravelScheduleCityResponse.builder()
                    .cityId(travelScheduleCity.getCity().getId())
                    .cityName(travelScheduleCity.getCity().getName())
                    .countryName(travelScheduleCity.getCity().getCountry().getName())
                    .build();
        }
    }

    public static TravelScheduleResponse from(TravelSchedule travelSchedule) {
        return TravelScheduleResponse.builder()
                .startDate(travelSchedule.getStartDate())
                .endDate(travelSchedule.getEndDate())
                .travelScheduleCities(travelSchedule.getTravelScheduleCities().stream()
                        .map(TravelScheduleCityResponse::from)
                        .toList())
                .build();
    }
}
