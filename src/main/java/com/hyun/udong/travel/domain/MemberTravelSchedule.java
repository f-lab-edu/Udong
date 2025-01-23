package com.hyun.udong.travel.domain;

import com.hyun.udong.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class MemberTravelSchedule extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "travel_schedule_id")
    private Long id;

    private LocalDate startDate;

    private LocalDate endDate;

    @OneToMany(mappedBy = "travelSchedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TravelScheduleCity> travelScheduleCities = new ArrayList<>();

    @Builder
    public MemberTravelSchedule(LocalDate startDate, LocalDate endDate, List<TravelScheduleCity> travelScheduleCities) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.travelScheduleCities = travelScheduleCities;
    }
}
