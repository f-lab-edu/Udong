package com.hyun.udong.travelschedule.application.service;

import com.hyun.udong.member.domain.Member;
import com.hyun.udong.member.domain.SocialType;
import com.hyun.udong.member.exception.MemberNotFoundException;
import com.hyun.udong.member.infrastructure.repository.MemberRepository;
import com.hyun.udong.travelschedule.domain.TravelSchedule;
import com.hyun.udong.travelschedule.exception.CityNotFoundException;
import com.hyun.udong.travelschedule.exception.TravelScheduleNotFoundException;
import com.hyun.udong.travelschedule.presentation.dto.TravelScheduleRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

@SpringBootTest
class TravelScheduleServiceTest {

    public static final long FIRST_MEMBER_ID = 1L;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TravelScheduleService travelScheduleService;

    @BeforeEach
    void setUp() {
        Member member = new Member(1L, SocialType.KAKAO, "hyun", "profile_image");
        memberRepository.save(member);
    }

    private TravelScheduleRequest createTravelScheduleRequest(Long... cityIds) {
        return new TravelScheduleRequest(LocalDate.now(), LocalDate.now().plusDays(5), List.of(cityIds));
    }

    @DisplayName("유효한 회원 ID와 유효한 요청으로 여행 일정을 등록한다.")
    @Test
    void updateTravelSchedule_validMemberAndRequest() {
        // given
        TravelScheduleRequest request = createTravelScheduleRequest(1L, 2L);

        // when
        TravelSchedule travelSchedule = travelScheduleService.updateTravelSchedule(FIRST_MEMBER_ID, request);

        // then
        then(travelSchedule).isNotNull();
        then(travelSchedule.getTravelScheduleCities()).hasSize(2);
        then(travelSchedule.getTravelScheduleCities().get(0).getCity().getName()).isEqualTo("Seoul");
        then(travelSchedule.getTravelScheduleCities().get(1).getCity().getName()).isEqualTo("Busan");
    }

    @DisplayName("존재하지 않는 회원 ID로 여행 일정을 등록할 때 예외가 발생한다.")
    @Test
    void updateTravelSchedule_invalidMemberId() {
        // given
        Long memberId = 999L;
        TravelScheduleRequest request = createTravelScheduleRequest(1L, 2L);

        // when & then
        thenThrownBy(() -> travelScheduleService.updateTravelSchedule(memberId, request))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @DisplayName("존재하지 않는 도시 ID로 여행 일정을 등록할 때 예외가 발생한다.")
    @Test
    void updateTravelSchedule_invalidCityId() {
        // given
        Long memberId = 1L;
        TravelScheduleRequest request = createTravelScheduleRequest(1L, 999L);
        Member member = new Member(memberId, SocialType.KAKAO, "hyun", "profile_image");
        memberRepository.save(member);

        // when & then
        thenThrownBy(() -> travelScheduleService.updateTravelSchedule(memberId, request))
                .isInstanceOf(CityNotFoundException.class);
    }

    @DisplayName("유효한 회원 ID로 여행 일정을 조회한다.")
    @Test
    @Transactional
    void findTravelSchedule_ok() {
        // given
        Long memberId = 1L;
        TravelScheduleRequest request = createTravelScheduleRequest(1L, 2L);
        travelScheduleService.updateTravelSchedule(FIRST_MEMBER_ID, request);

        // when
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> MemberNotFoundException.EXCEPTION);
        TravelSchedule travelSchedule = member.getTravelSchedule();

        // then
        then(travelSchedule).isNotNull();
        then(travelSchedule.getStartDate()).isEqualTo(request.getStartDate());
        then(travelSchedule.getEndDate()).isEqualTo(request.getEndDate());
        then(travelSchedule.getTravelScheduleCities()).hasSize(request.getCityIds().size());
    }

    @DisplayName("존재하지 않는 회원 ID로 여행 일정을 조회할 때 예외가 발생한다.")
    @Test
    void findTravelSchedule_noMember_throw() {
        // given
        Long memberId = 999L;

        // when & then
        thenThrownBy(() -> travelScheduleService.findTravelSchedule(memberId))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @DisplayName("회원의 여행 일정이 없을 때 예외가 발생한다.")
    @Test
    void findTravelSchedule_noTravelSchedule_throw() {
        // when & then
        thenThrownBy(() -> travelScheduleService.findTravelSchedule(FIRST_MEMBER_ID))
                .isInstanceOf(TravelScheduleNotFoundException.class);
    }
}
