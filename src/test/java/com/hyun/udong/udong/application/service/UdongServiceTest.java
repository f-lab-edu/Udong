package com.hyun.udong.udong.application.service;

import com.hyun.udong.common.dto.PagedResponse;
import com.hyun.udong.common.exception.NotFoundException;
import com.hyun.udong.common.util.DataCleanerExtension;
import com.hyun.udong.member.domain.Member;
import com.hyun.udong.member.domain.SocialType;
import com.hyun.udong.member.infrastructure.repository.MemberRepository;
import com.hyun.udong.udong.domain.Participant;
import com.hyun.udong.udong.domain.Udong;
import com.hyun.udong.udong.domain.UdongStatus;
import com.hyun.udong.udong.domain.WaitingMember;
import com.hyun.udong.udong.exception.InvalidParticipationException;
import com.hyun.udong.udong.infrastructure.repository.ParticipantRepository;
import com.hyun.udong.udong.infrastructure.repository.UdongRepository;
import com.hyun.udong.udong.infrastructure.repository.WaitingMemberRepository;
import com.hyun.udong.udong.presentation.dto.request.CreateUdongRequest;
import com.hyun.udong.udong.presentation.dto.request.FindUdongsCondition;
import com.hyun.udong.udong.presentation.dto.response.CreateUdongResponse;
import com.hyun.udong.udong.presentation.dto.response.SimpleUdongResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

@ExtendWith(DataCleanerExtension.class)
@SpringBootTest
class UdongServiceTest {

    private static final long NOT_EXISTS_CITY_ID = 999L;
    private static final long NOT_EXISTS_UDONG_ID = 999L;
    private static final long CITY_ID_OF_SEOUL = 1L;
    private static final long CITY_ID_OF_BUSAN = 2L;

    private Udong udong;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private UdongService udongService;

    @Autowired
    private UdongRepository udongRepository;

    @Autowired
    private WaitingMemberRepository waitingMemberRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @BeforeEach
    void setUp() {
        Member owner = memberRepository.save(new Member(1L, SocialType.KAKAO, "hyun", "profile_image"));
        udong = udongRepository.save(new Udong(owner.getId(),
                "title",
                "description",
                5,
                LocalDate.now(),
                LocalDate.now().plusDays(5),
                UdongStatus.PREPARE));
    }

    @Test
    @Transactional
    void 모집글을_생성한다() {
        // given
        Member member = memberRepository.save(new Member(1L, SocialType.KAKAO, "hyun", "profile_image"));
        CreateUdongRequest request = new CreateUdongRequest(
                List.of(CITY_ID_OF_SEOUL, CITY_ID_OF_BUSAN),
                "동행 구해요",
                "서울과 부산 여행할 동행을 찾습니다!",
                5,
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(10),
                List.of("여행", "맛집"));

        // when
        CreateUdongResponse response = udongService.createUdong(request, member.getId());
        Udong savedUdong = udongRepository.findById(response.getId()).orElseThrow();

        // then
        then(response).isNotNull();
        then(response.getOwnerId()).isEqualTo(member.getId());
        then(response.getTitle()).isEqualTo(request.getTitle());
        then(response.getDescription()).isEqualTo(request.getDescription());
        then(response.getStartDate()).isEqualTo(request.getStartDate());
        then(response.getEndDate()).isEqualTo(request.getEndDate());
        then(response.getTags()).isEqualTo(Set.copyOf(request.getTags()));
        then(response.getCurrentParticipantsCount()).isEqualTo(1);
        then(response.getParticipants()).hasSize(1);
        then(response.getParticipants().get(0).id()).isEqualTo(member.getId());
        then(response.getParticipants().get(0).name()).isEqualTo(member.getNickname());
        then(savedUdong.getTravelCities().size()).isEqualTo(2);
        then(savedUdong.getTravelCities().get(0).getId()).isEqualTo(CITY_ID_OF_SEOUL);
        then(savedUdong.getTravelCities().get(1).getId()).isEqualTo(CITY_ID_OF_BUSAN);
    }

    @Test
    void 존재하지_않는_도시_ID로_모집글을_생성하면_예외발생() {
        // given
        Member member = memberRepository.save(new Member(1L, SocialType.KAKAO, "hyun", "profile_image"));
        CreateUdongRequest request = new CreateUdongRequest(
                List.of(NOT_EXISTS_CITY_ID),
                "동행 구해요",
                "서울과 부산 여행할 동행을 찾습니다!",
                5,
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(10),
                List.of("여행", "맛집"));

        // when & then
        thenThrownBy(() -> udongService.createUdong(request, member.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 도시가 존재하지 않습니다.");
    }

    @Test
    @Sql(scripts = "/insert_udong_data.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void 검색_조건_없이_전체_우동_조회() {
        // given
        FindUdongsCondition searchCondition = new FindUdongsCondition(null, null, null, null, null);

        // when
        PagedResponse<SimpleUdongResponse> udongs = udongService.findUdongs(searchCondition, Pageable.ofSize(20));

        // then
        assertThat(udongs).isNotNull();
        assertThat(udongs.content()).hasSize(20);
    }

    @Test
    @Sql(scripts = "/insert_udong_data.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void 특정_기간_필터링_우동_조회() {
        // given
        LocalDate startDate = LocalDate.of(2025, 11, 1);
        LocalDate endDate = LocalDate.of(2025, 11, 10);
        FindUdongsCondition searchCondition = new FindUdongsCondition(null, null, startDate, endDate, null);

        // when
        PagedResponse<SimpleUdongResponse> result = udongService.findUdongs(searchCondition, Pageable.ofSize(20));

        // then
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(18);
        assertThat(result.content().stream()
                .allMatch(udong -> udong.getStartDate().isEqual(startDate)
                        && udong.getEndDate().isEqual(endDate))).isTrue();
    }

    @Test
    void 멤버가_우동에_동행_요청을_보냄() {
        // given
        Member requestMember = memberRepository.save(new Member(2L, SocialType.KAKAO, "gildong", "profile_image"));

        // when
        udongService.requestParticipation(udong.getId(), requestMember.getId());

        // then
        boolean isExistsMember = waitingMemberRepository.existsByUdongAndMemberId(udong, requestMember.getId());
        assertThat(isExistsMember).isTrue();
    }

    @Test
    void 이미_참여_중인_우동에_동행_요청을_보내면_예외가_발생한다() {
        // given
        Member requestMember = memberRepository.save(new Member(2L, SocialType.KAKAO, "gildong", "profile_image"));
        participantRepository.save(Participant.from(requestMember.getId(), udong));

        // when & then
        assertThatThrownBy(() -> udongService.requestParticipation(udong.getId(), requestMember.getId()))
                .isInstanceOf(InvalidParticipationException.class)
                .hasMessage("이미 참여 중인 우동입니다.");
    }

    @Test
    void 이미_대기_중인_우동에_동행_요청을_보내면_예외가_발생한다() {
        // given
        Member requestMember = memberRepository.save(new Member(2L, SocialType.KAKAO, "gildong", "profile_image"));
        waitingMemberRepository.save(new WaitingMember(udong, requestMember.getId()));

        // when & then
        assertThatThrownBy(() -> udongService.requestParticipation(udong.getId(), requestMember.getId()))
                .isInstanceOf(InvalidParticipationException.class)
                .hasMessage("이미 요청을 보낸 우동입니다.");
    }

    @Test
    void 존재하지_않는_우동에_동행_요청을_보내면_예외가_발생한다() {
        // given
        Member requestMember = memberRepository.save(new Member(2L, SocialType.KAKAO, "gildong", "profile_image"));

        // when & then
        assertThatThrownBy(() ->
                udongService.requestParticipation(NOT_EXISTS_UDONG_ID, requestMember.getId())
        )
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 우동입니다.");
    }

    @Test
    void 존재하지_않는_우동에_대기자_승인을_하면_예외가_발생한다() {
        // given
        Member requestMember = memberRepository.save(new Member(2L, SocialType.KAKAO, "gildong", "profile_image"));
        waitingMemberRepository.save(new WaitingMember(udong, requestMember.getId()));

        // when & then
        assertThatThrownBy(() -> udongService.approveParticipant(NOT_EXISTS_UDONG_ID, requestMember.getId(), udong.getOwnerId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 우동입니다.");
    }

    @Test
    void 존재하지_않는_대기자를_승인하면_예외가_발생한다() {
        // given
        Member requestMember = memberRepository.save(new Member(2L, SocialType.KAKAO, "gildong", "profile_image"));

        // when & then
        assertThatThrownBy(() -> udongService.approveParticipant(udong.getId(), requestMember.getId(), udong.getOwnerId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 대기자를 찾을 수 없습니다.");
    }

    @Test
    void 모임장이_아닌_멤버가_대기자를_승인하면_예외가_발생한다() {
        // given
        Member requestMember = memberRepository.save(new Member(2L, SocialType.KAKAO, "gildong", "profile_image"));
        waitingMemberRepository.save(new WaitingMember(udong, requestMember.getId()));

        Member notOwner = memberRepository.save(new Member(3L, SocialType.KAKAO, "gildong2", "profile_image"));

        // when & then
        assertThatThrownBy(() -> udongService.approveParticipant(udong.getId(), requestMember.getId(), notOwner.getId()))
                .isInstanceOf(InvalidParticipationException.class)
                .hasMessage("승인/거부할 권한이 없습니다.");
    }

    @Test
    void 모임장이_대기자를_승인하면_참여자로_등록된다() {
        // given
        Member requestMember = memberRepository.save(new Member(2L, SocialType.KAKAO, "gildong", "profile_image"));
        waitingMemberRepository.save(new WaitingMember(udong, requestMember.getId()));

        // when
        udongService.approveParticipant(udong.getId(), requestMember.getId(), udong.getOwnerId());

        // then
        boolean isParticipant = participantRepository.existsByUdongAndMemberId(udong, requestMember.getId());
        assertThat(isParticipant).isTrue();
    }

    @Test
    void 모임장이_대기자를_거부하면_대기자가_삭제된다() {
        // given
        Member requestMember = memberRepository.save(new Member(2L, SocialType.KAKAO, "gildong", "profile_image"));
        waitingMemberRepository.save(new WaitingMember(udong, requestMember.getId()));

        // when
        udongService.rejectParticipant(udong.getId(), requestMember.getId(), udong.getOwnerId());

        // then
        boolean isExistsWaitingMember = waitingMemberRepository.existsByUdongAndMemberId(udong, requestMember.getId());
        assertThat(isExistsWaitingMember).isFalse();
    }
}
