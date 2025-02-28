package com.hyun.udong.udong.application.service;

import com.hyun.udong.common.dto.PagedResponse;
import com.hyun.udong.common.exception.NotFoundException;
import com.hyun.udong.member.domain.Member;
import com.hyun.udong.member.infrastructure.repository.MemberRepository;
import com.hyun.udong.travelschedule.domain.City;
import com.hyun.udong.travelschedule.infrastructure.repository.CityRepository;
import com.hyun.udong.udong.domain.*;
import com.hyun.udong.udong.exception.InvalidParticipationException;
import com.hyun.udong.udong.infrastructure.repository.UdongRepository;
import com.hyun.udong.udong.infrastructure.repository.participant.ParticipantRepository;
import com.hyun.udong.udong.infrastructure.repository.waitingmember.WaitingMemberRepository;
import com.hyun.udong.udong.presentation.dto.request.CreateUdongRequest;
import com.hyun.udong.udong.presentation.dto.request.FindUdongsCondition;
import com.hyun.udong.udong.presentation.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
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
    private final WaitingMemberRepository waitingMemberRepository;

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
        Page<Udong> udongs = udongRepository.findByFilter(request, pageable);
        List<ParticipantCountResponse> counts = getParticipantCounts(udongs.getContent());
        List<SimpleUdongResponse> responses = mapToResponses(udongs.getContent(), counts);
        return PagedResponse.of(new PageImpl<>(responses, pageable, udongs.getTotalElements()));
    }

    @Transactional
    public WaitingMemberResponse requestParticipation(Long udongId, Long memberId) {
        Udong udong = findUdongById(udongId);

        validateParticipationRequest(memberId, udong);

        WaitingMember waitingMember = WaitingMember.of(udong, memberId);
        return WaitingMemberResponse.of(waitingMemberRepository.save(waitingMember));
    }

    @Retryable(
            retryFor = ObjectOptimisticLockingFailureException.class,
            maxAttempts = 5,
            backoff = @Backoff(delay = 50)
    )
    @Transactional
    public WaitingMemberResponse requestParticipationWithLock(Long udongId, Long memberId) {
        Udong udong = udongRepository.findUdongByWithOptimisticLock(udongId);
        validateParticipationRequest(memberId, udong);

        WaitingMember waitingMember = WaitingMember.of(udong, memberId);
        WaitingMember saved = waitingMemberRepository.save(waitingMember);

        udong.increaseWaitingMemberCount();
        return WaitingMemberResponse.of(saved);
    }

    @Transactional
    public ApprovedParticipantResponse approveParticipant(Long udongId, Long waitingMemberId, Long ownerId) {
        Udong udong = findUdongById(udongId);
        udong.validateOwner(ownerId);

        WaitingMember waitingMember = findWaitingMember(waitingMemberId, udong);
        waitingMemberRepository.delete(waitingMember);

        return ApprovedParticipantResponse.of(participantRepository.save(Participant.from(waitingMember.getMemberId(), udong)));
    }

    @Transactional
    public void rejectParticipant(Long udongId, Long waitingMemberId, Long ownerId) {
        Udong udong = findUdongById(udongId);
        udong.validateOwner(ownerId);

        WaitingMember waitingMember = findWaitingMember(waitingMemberId, udong);
        waitingMemberRepository.delete(waitingMember);
    }

    private List<ParticipantCountResponse> getParticipantCounts(List<Udong> udongs) {
        List<Long> udongIds = udongs.stream()
                .map(Udong::getId)
                .toList();

        return participantRepository.countParticipantsByUdongIds(udongIds);
    }

    private List<SimpleUdongResponse> mapToResponses(List<Udong> udongs, List<ParticipantCountResponse> counts) {
        Map<Long, Integer> udongIdToCount = counts.stream()
                .collect(Collectors.toMap(ParticipantCountResponse::udongId, count -> count.participantCount().intValue()));

        return udongs.stream()
                .map(udong -> SimpleUdongResponse.from(udong, udongIdToCount.getOrDefault(udong.getId(), 0)))
                .toList();
    }

    private void validateParticipationRequest(Long memberId, Udong udong) {
        List<Participant> participants = participantRepository.findByUdong(udong);
        boolean alreadyParticipated = participants.stream()
                .anyMatch(participant -> participant.getMemberId().equals(memberId));

        if (alreadyParticipated) {
            throw new InvalidParticipationException("이미 참여 중인 우동입니다.");
        }

        if (waitingMemberRepository.existsByUdongAndMemberId(udong, memberId)) {
            throw new InvalidParticipationException("이미 요청을 보낸 우동입니다.");
        }

        udong.validateParticipation(memberId, participants.size());
    }

    private Udong findUdongById(Long udongId) {
        return udongRepository.findById(udongId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 우동입니다."));
    }

    private WaitingMember findWaitingMember(Long waitingMemberId, Udong udong) {
        return waitingMemberRepository.findByUdongAndMemberId(udong, waitingMemberId)
                .orElseThrow(() -> new NotFoundException("해당 대기자를 찾을 수 없습니다."));
    }
}
