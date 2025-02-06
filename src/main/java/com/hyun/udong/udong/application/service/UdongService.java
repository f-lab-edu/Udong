package com.hyun.udong.udong.application.service;

import com.hyun.udong.common.exception.NotFoundException;
import com.hyun.udong.member.domain.Member;
import com.hyun.udong.member.infrastructure.repository.MemberRepository;
import com.hyun.udong.travelschedule.domain.City;
import com.hyun.udong.travelschedule.infrastructure.repository.CityRepository;
import com.hyun.udong.udong.domain.*;
import com.hyun.udong.udong.infrastructure.repository.UdongRepository;
import com.hyun.udong.udong.presentation.dto.CreateUdongRequest;
import com.hyun.udong.udong.presentation.dto.UdongResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UdongService {

    private final MemberRepository memberRepository;
    private final CityRepository cityRepository;
    private final UdongRepository udongRepository;

    public UdongResponse createUdong(CreateUdongRequest request, Member loginMember) {
        List<City> cities = cityRepository.findAllById(request.getCityIds());
        if (cities.size() != request.getCityIds().size()) {
            throw new NotFoundException("해당 도시가 존재하지 않습니다.");
        }

        Udong udong = Udong.builder()
                .content(Content.of(request.getTitle(), request.getDescription()))
                .participants(Participants.from(loginMember.getId()))
                .recruitPlanner(RecruitPlanner.from(request.getRecruitmentCount()))
                .travelPlanner(TravelPlanner.of(request.getStartDate(), request.getEndDate()))
                .attachedTags(AttachedTags.of(request.getTags()))
                .build();

        udong.addCities(cities);

        Udong savedUdong = udongRepository.save(udong);

        List<Member> participants = memberRepository.findAllById(
                savedUdong.getParticipants().getParticipants().stream()
                        .map(Participant::getMemberId)
                        .toList());
        return UdongResponse.from(udong, participants);
    }
}
