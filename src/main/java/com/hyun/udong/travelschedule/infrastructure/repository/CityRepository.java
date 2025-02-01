package com.hyun.udong.travelschedule.infrastructure.repository;

import com.hyun.udong.travelschedule.domain.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Long> {
}
