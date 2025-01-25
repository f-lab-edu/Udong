package com.hyun.udong.travelschedule.application.service;

import com.hyun.udong.member.domain.Member;
import com.hyun.udong.member.exception.MemberNotFoundException;
import com.hyun.udong.member.infrastructure.repository.MemberRepository;
import com.hyun.udong.travelschedule.domain.City;
import com.hyun.udong.travelschedule.domain.MemberTravelSchedule;
import com.hyun.udong.travelschedule.domain.TravelScheduleCity;
import com.hyun.udong.travelschedule.exception.CityNotFoundException;
import com.hyun.udong.travelschedule.exception.MemberTravelScheduleNotFoundException;
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
    public MemberTravelSchedule updateTravelSchedule(Long memberId, TravelScheduleRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> MemberNotFoundException.EXCEPTION);

        MemberTravelSchedule travelSchedule = MemberTravelSchedule.builder()
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        List<TravelScheduleCity> travelScheduleCities = request.getCityIds().stream()
                .map(cityId -> {
                    City city = cityRepository.findById(cityId)
                            .orElseThrow(() -> CityNotFoundException.EXCEPTION);
                    return new TravelScheduleCity(travelSchedule, city);
                })
                .toList();
        travelSchedule.addTravelScheduleCity(travelScheduleCities);
        travelScheduleRepository.save(travelSchedule);

        member.updateTravelSchedule(travelSchedule);

        return travelSchedule;
    }

    public MemberTravelSchedule findTravelSchedule(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> MemberNotFoundException.EXCEPTION);

        MemberTravelSchedule travelSchedule = member.getTravelSchedule();
        if (travelSchedule == null) {
            throw MemberTravelScheduleNotFoundException.EXCEPTION;
        }
        return travelSchedule;
    }
}
