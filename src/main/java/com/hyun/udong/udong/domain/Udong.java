package com.hyun.udong.udong.domain;

import com.hyun.udong.common.entity.BaseTimeEntity;
import com.hyun.udong.common.exception.InvalidParameterException;
import com.hyun.udong.travelschedule.domain.City;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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

    @Column(nullable = false)
    private Long ownerId;

    @Embedded
    private Content content;

    @Embedded
    private RecruitPlanner recruitPlanner;

    @Embedded
    private TravelPlanner travelPlanner;

    @Embedded
    private AttachedTags attachedTags;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UdongStatus status;

    @OneToMany(mappedBy = "udong", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TravelCity> travelCities = new ArrayList<>();


    @Builder
    public Udong(Content content,
                 RecruitPlanner recruitPlanner,
                 TravelPlanner travelPlanner,
                 AttachedTags attachedTags,
                 Long ownerId) {
        if (ownerId == null) {
            throw new InvalidParameterException("ownerId는 필수값입니다.");
        }
        this.ownerId = ownerId;
        this.content = content;
        this.recruitPlanner = recruitPlanner;
        this.travelPlanner = travelPlanner;
        this.attachedTags = attachedTags;
        if (travelPlanner.getStartDate().isEqual(LocalDate.now())) {
            this.status = UdongStatus.IN_PROGRESS;
        } else {
            this.status = UdongStatus.PREPARE;
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
}
