package com.hyun.udong.udong.domain;

import com.hyun.udong.common.exception.InvalidParameterException;
import com.hyun.udong.udong.exception.InvalidParticipationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UdongTest {

    private Content content;
    private RecruitPlanner recruitPlanner;
    private TravelPlanner travelPlanner;
    private AttachedTags attachedTags;

    @BeforeEach
    void setUp() {
        content = Content.of("여행 동행 모집", "즐겁게 여행할 동행을 구합니다.");
        recruitPlanner = RecruitPlanner.from(5);
        travelPlanner = TravelPlanner.of(LocalDate.now().plusDays(5), LocalDate.now().plusDays(10));
        attachedTags = AttachedTags.of(List.of("자연", "배낭여행"));
    }

    private Udong createUdong(Content content, RecruitPlanner recruitPlanner, TravelPlanner travelPlanner, AttachedTags attachedTags) {
        return Udong.builder()
                .content(content)
                .recruitPlanner(recruitPlanner)
                .travelPlanner(travelPlanner)
                .attachedTags(attachedTags)
                .ownerId(1L)
                .build();
    }

    private static Udong createUdong(UdongStatus status) {
        return new Udong(1L,
                "title",
                "description",
                5,
                LocalDate.now(),
                LocalDate.now().plusDays(5),
                status);
    }

    @Test
    void 우동이_정상적으로_생성된다() {
        Udong udong = createUdong(content, recruitPlanner, travelPlanner, attachedTags);

        assertThat(udong.getContent().getTitle()).isEqualTo("여행 동행 모집");
        assertThat(udong.getRecruitPlanner().getRecruitmentCount()).isEqualTo(5);
        assertThat(udong.getTravelPlanner().getStartDate()).isEqualTo(LocalDate.now().plusDays(5));
        assertThat(udong.getAttachedTags().getTags()).contains("자연", "배낭여행");
    }

    @Test
    void 우동_생성_시_ownerId가_정상적으로_설정된다() {
        Udong udong = createUdong(content, recruitPlanner, travelPlanner, attachedTags);

        assertThat(udong.getOwnerId()).isEqualTo(1L);
    }

    @Test
    void void_우동_생성_시_ownerId가_null일_경우_예외가_발생한다() {
        assertThatThrownBy(() -> Udong.builder()
                .content(content)
                .recruitPlanner(recruitPlanner)
                .travelPlanner(travelPlanner)
                .attachedTags(attachedTags)
                .ownerId(null)
                .build())
                .isInstanceOf(InvalidParameterException.class);
    }

    @Test
    void 여행_시작일이_오늘일_경우_상태는_IN_PROGRESS로_설정된다() {
        TravelPlanner todayTravel = TravelPlanner.of(LocalDate.now(), LocalDate.now().plusDays(3));

        Udong udong = createUdong(content, recruitPlanner, todayTravel, attachedTags);

        assertThat(udong.getStatus()).isEqualTo(UdongStatus.IN_PROGRESS);
    }

    @Test
    void 여행_시작일이_미래일_경우_상태는_PREPARE로_설정된다() {
        Udong udong = createUdong(content, recruitPlanner, travelPlanner, attachedTags);

        assertThat(udong.getStatus()).isEqualTo(UdongStatus.PREPARE);
    }

    @Test
    void 진행전이_아닌_우동에는_참여할_수_없다() {
        Udong udong = createUdong(UdongStatus.IN_PROGRESS);
        Long memberId = 2L;

        assertThatThrownBy(() -> udong.validateParticipation(memberId, 0))
                .isInstanceOf(InvalidParticipationException.class)
                .hasMessage("여행이 시작되었거나 종료된 우동에는 참여할 수 없습니다.");
    }

    @Test
    void 자신이_생성한_우동에는_참여할_수_없다() {
        Udong udong = createUdong(UdongStatus.PREPARE);
        Long ownerId = udong.getOwnerId();

        // when & then
        assertThatThrownBy(() -> udong.validateParticipation(ownerId, 0))
                .isInstanceOf(InvalidParticipationException.class)
                .hasMessage("자신이 생성한 우동에는 참여할 수 없습니다.");
    }
}
