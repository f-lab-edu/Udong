package com.hyun.udong.udong.application.service;

import com.hyun.udong.common.exception.NotFoundException;
import com.hyun.udong.common.util.DataCleanerExtension;
import com.hyun.udong.member.domain.Member;
import com.hyun.udong.member.domain.SocialType;
import com.hyun.udong.member.infrastructure.repository.MemberRepository;
import com.hyun.udong.udong.domain.Udong;
import com.hyun.udong.udong.infrastructure.repository.UdongRepository;
import com.hyun.udong.udong.presentation.dto.CreateUdongRequest;
import com.hyun.udong.udong.presentation.dto.UdongResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

@ExtendWith(DataCleanerExtension.class)
@SpringBootTest
class UdongServiceTest {

    public static final long NOT_EXISTS_MEMBER_ID = 999L;
    public static final long NOT_EXISTS_CITY_ID = 999L;
    public static final long CITY_ID_OF_SEOUL = 1L;
    public static final long CITY_ID_OF_BUSAN = 2L;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private UdongService udongService;

    @Autowired
    private UdongRepository udongRepository;

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
        UdongResponse response = udongService.createUdong(request, member.getId());
        Udong savedUdong = udongRepository.findById(response.getId()).orElseThrow();

        // then
        then(response).isNotNull();
        then(response.getOwnerId()).isEqualTo(member.getId());
        then(response.getTitle()).isEqualTo(request.getTitle());
        then(response.getDescription()).isEqualTo(request.getDescription());
        then(response.getStartDate()).isEqualTo(request.getStartDate());
        then(response.getEndDate()).isEqualTo(request.getEndDate());
        then(response.getTags()).isEqualTo(Set.copyOf(request.getTags()));
        then(response.getCurrentMemberCount()).isEqualTo(1);
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
    void 존재하지_않는_회원으로_모집글을_생성하면_예외발생() {
        // given
        CreateUdongRequest request = new CreateUdongRequest(
                List.of(1L, 2L),
                "동행 구해요",
                "서울과 부산 여행할 동행을 찾습니다!",
                5,
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(10),
                List.of("여행", "맛집"));

        // when & then
        thenThrownBy(() -> udongService.createUdong(request, NOT_EXISTS_MEMBER_ID))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 회원이 존재하지 않습니다.");
    }
}
