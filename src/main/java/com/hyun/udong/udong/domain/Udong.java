package com.hyun.udong.udong.domain;

import com.hyun.udong.common.entity.BaseTimeEntity;
import com.hyun.udong.travelschedule.domain.City;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Udong extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "udong_id")
    private Long id;

    @Embedded
    private Content content;

    @Embedded
    private Participants participants;

    @Embedded
    private RecruitPlanner recruitPlanner;

    @Embedded
    private TravelPlanner travelPlanner;

    @Embedded
    private AttachedTags attachedTags;

    @Embedded
    private WaitingMembers waitingMembers;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UdongStatus status;

    @OneToMany(mappedBy = "udong", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TravelCity> travelCities = new ArrayList<>();

    @Builder
    public Udong(Content content,
                 Participants participants,
                 RecruitPlanner recruitPlanner,
                 TravelPlanner travelPlanner,
                 AttachedTags attachedTags) {
        this(content, participants, recruitPlanner, travelPlanner, attachedTags, WaitingMembers.empty(), UdongStatus.PREPARE);
    }

    private Udong(Content content,
                  Participants participants,
                  RecruitPlanner recruitPlanner,
                  TravelPlanner travelPlanner,
                  AttachedTags attachedTags,
                  WaitingMembers waitingMembers,
                  UdongStatus status) {
        this.content = content;
        this.participants = participants;
        this.recruitPlanner = recruitPlanner;
        this.travelPlanner = travelPlanner;
        this.attachedTags = attachedTags;
        this.waitingMembers = waitingMembers;
        if (travelPlanner.getStartDate().isEqual(LocalDate.now())) {
            this.status = UdongStatus.IN_PROGRESS;
        } else {
            this.status = status;
        }
    }

    public void addCities(List<City> cities) {
        cities.forEach(this::addCity);
    }

    private void addCity(City city) {
        boolean isDuplicate = travelCities.stream()
                .anyMatch(travelCity -> travelCity.getCity().getId().equals(city.getId()));

        if (!isDuplicate) {
            TravelCity travelCity = new TravelCity(this, city);
            travelCities.add(travelCity);
        }
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return super.getCreatedAt();
    }
}
