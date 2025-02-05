package com.hyun.udong.udong.domain;

import com.hyun.udong.common.entity.BaseTimeEntity;
import com.hyun.udong.travelschedule.domain.City;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private List<UdongCity> travelCities = new ArrayList<>();

    public void addCities(List<City> cities) {
        cities.forEach(this::addCity);
    }

    public void addCity(City city) {
        boolean isDuplicate = travelCities.stream().anyMatch(udongCity -> udongCity.getCity().getId().equals(city.getId()));
        if (!isDuplicate) {
            UdongCity udongCity = new UdongCity(this, city);
            travelCities.add(udongCity);
        }
    }

}
