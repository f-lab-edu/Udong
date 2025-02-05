package com.hyun.udong.udong.domain;

import com.hyun.udong.travelschedule.domain.City;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

import static lombok.AccessLevel.PROTECTED;

@Embeddable
@Getter
@NoArgsConstructor(access = PROTECTED)
public class TravelCities {

    @ElementCollection
    @CollectionTable(name = "udong_travel_city", joinColumns = @JoinColumn(name = "udong_id"))
    private Set<City> cities;

    @Builder
    public TravelCities(Set<City> cities) {
        this.cities = cities;
    }
}
