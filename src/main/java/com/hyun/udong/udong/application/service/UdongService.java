package com.hyun.udong.udong.application.service;

import com.hyun.udong.common.dto.PagedResponse;
import com.hyun.udong.common.exception.NotFoundException;
import com.hyun.udong.member.domain.Member;
import com.hyun.udong.member.infrastructure.repository.MemberRepository;
import com.hyun.udong.travelschedule.domain.City;
import com.hyun.udong.travelschedule.infrastructure.repository.CityRepository;
import com.hyun.udong.udong.domain.*;
import com.hyun.udong.udong.infrastructure.repository.ParticipantRepository;
import com.hyun.udong.udong.infrastructure.repository.UdongRepository;
import com.hyun.udong.udong.presentation.dto.request.CreateUdongRequest;
import com.hyun.udong.udong.presentation.dto.request.FindUdongsCondition;
import com.hyun.udong.udong.presentation.dto.response.CreateUdongResponse;
import com.hyun.udong.udong.presentation.dto.response.SimpleUdongResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UdongService {

    private final MemberRepository memberRepository;
    private final CityRepository cityRepository;
    private final UdongRepository udongRepository;
    private final ParticipantRepository participantRepository;

    @Transactional
    public CreateUdongResponse createUdong(CreateUdongRequest request, Long memberId) {
        List<City> cities = cityRepository.findAllById(request.getCityIds());
        if (cities.size() != request.getCityIds().size()) {
            throw new NotFoundException("해당 도시가 존재하지 않습니다.");
        }

        Udong udong = Udong.builder()
                .content(Content.of(request.getTitle(), request.getDescription()))
                .recruitPlanner(RecruitPlanner.from(request.getRecruitmentCount()))
                .travelPlanner(TravelPlanner.of(request.getStartDate(), request.getEndDate()))
                .attachedTags(AttachedTags.of(request.getTags()))
                .ownerId(memberId)
                .build();

        udong.addCities(cities);
        udongRepository.save(udong);
        Participant owner = participantRepository.save(Participant.from(memberId, udong));

        List<Member> participantMembers = memberRepository.findAllById(List.of(owner.getMemberId()));
        return CreateUdongResponse.from(udong, participantMembers);
    }

    public PagedResponse<SimpleUdongResponse> findUdongs(FindUdongsCondition request, Pageable pageable) {
        Page<Udong> udongPage = udongRepository.findByFilter(request, pageable);

        List<Long> udongIds = udongPage.getContent().stream()
                .map(Udong::getId)
                .toList();

        Map<Long, Integer> participantCounts = participantRepository.countParticipantsByUdongIds(udongIds).stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> ((Number) row[1]).intValue()
                ));

        List<SimpleUdongResponse> udongResponses = udongPage.getContent().stream()
                .map(udong -> SimpleUdongResponse.from(
                        udong, participantCounts.getOrDefault(udong.getId(), 0)))
                .toList();
        return PagedResponse.of(new PageImpl<>(udongResponses, pageable, udongPage.getTotalElements()));
    }
}
