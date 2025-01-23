package com.hyun.udong.travel.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class TravelScheduleCity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "travel_schedule_id", nullable = false)
    private MemberTravelSchedule travelSchedule;

    @ManyToOne
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @Builder
    public TravelScheduleCity(MemberTravelSchedule travelSchedule, City city) {
        this.travelSchedule = travelSchedule;
        this.city = city;
    }
}
