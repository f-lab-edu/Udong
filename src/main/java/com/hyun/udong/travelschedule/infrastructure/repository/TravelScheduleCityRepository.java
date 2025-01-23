package com.hyun.udong.travelschedule.infrastructure.repository;

import com.hyun.udong.travelschedule.domain.TravelScheduleCity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelScheduleCityRepository extends JpaRepository<TravelScheduleCity, Long> {
}
