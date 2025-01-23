package com.hyun.udong.travelschedule.infrastructure.repository;

import com.hyun.udong.travelschedule.domain.MemberTravelSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelScheduleRepository extends JpaRepository<MemberTravelSchedule, Long> {
}
