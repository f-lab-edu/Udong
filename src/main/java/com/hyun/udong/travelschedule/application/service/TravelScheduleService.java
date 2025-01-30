package com.hyun.udong.travelschedule.application.service;

import com.hyun.udong.common.exception.NotFoundException;
import com.hyun.udong.member.domain.Member;
import com.hyun.udong.member.infrastructure.repository.MemberRepository;
import com.hyun.udong.travelschedule.domain.City;
import com.hyun.udong.travelschedule.domain.TravelSchedule;
import com.hyun.udong.travelschedule.domain.TravelScheduleCity;
import com.hyun.udong.travelschedule.infrastructure.repository.CityRepository;
import com.hyun.udong.travelschedule.infrastructure.repository.TravelScheduleRepository;
import com.hyun.udong.travelschedule.presentation.dto.TravelScheduleRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TravelScheduleService {

    private final MemberRepository memberRepository;
    private final TravelScheduleRepository travelScheduleRepository;
    private final CityRepository cityRepository;

    @Transactional
    public TravelSchedule updateTravelSchedule(Long memberId, TravelScheduleRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("해당 회원이 존재하지 않습니다."));

        TravelSchedule travelSchedule = TravelSchedule.builder()
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        List<City> cities = cityRepository.findAllById(request.getCityIds());
        if (cities.size() != request.getCityIds().size()) {
            throw new NotFoundException("해당 도시가 존재하지 않습니다.");
        }
        List<TravelScheduleCity> travelScheduleCities = cities.stream()
                .map(city -> new TravelScheduleCity(travelSchedule, city))
                .toList();
        travelSchedule.updateTravelScheduleCities(travelScheduleCities);
        travelScheduleRepository.save(travelSchedule);

        member.updateTravelSchedule(travelSchedule);

        return travelSchedule;
    }

    public TravelSchedule findTravelSchedule(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("해당 회원이 존재하지 않습니다."));

        TravelSchedule travelSchedule = member.getTravelSchedule();
        if (travelSchedule == null) {
            throw new NotFoundException("여행 일정이 존재하지 않습니다.");
        }
        return travelSchedule;
    }
}
