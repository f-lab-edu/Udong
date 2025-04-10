package com.hyun.udong.common.fixture;

import com.hyun.udong.member.domain.Member;
import com.hyun.udong.member.domain.SocialType;
import com.hyun.udong.udong.domain.*;

import java.time.LocalDate;
import java.util.List;

public class TestFixture {
    public static final Member HYUN = new Member(1L, SocialType.KAKAO, "hyun", "profile_image");
    public static final Udong UDONG1 = Udong.builder()
            .content(Content.of("여행 동행 모집", "즐겁게 여행할 동행을 구합니다."))
            .recruitPlanner(RecruitPlanner.from(5))
            .travelPlanner(TravelPlanner.of(LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 5)))
            .attachedTags(AttachedTags.of(List.of("자연", "배낭여행")))
            .ownerId(1L)
            .build();

    public static final Udong UDONG2 = Udong.builder()
            .content(Content.of("맛집 동행 모집", "맛집 투어 동행을 구합니다."))
            .recruitPlanner(RecruitPlanner.from(4))
            .travelPlanner(TravelPlanner.of(LocalDate.of(2025, 12, 6), LocalDate.of(2025, 12, 10)))
            .attachedTags(AttachedTags.of(List.of("음식", "맛집")))
            .ownerId(2L)
            .build();

    public static final Udong UDONG3 = Udong.builder()
            .content(Content.of("액티비티 투어 동행 모집", "액티비티 투어 동행을 구합니다."))
            .recruitPlanner(RecruitPlanner.from(3))
            .travelPlanner(TravelPlanner.of(LocalDate.of(2025, 12, 2), LocalDate.of(2025, 12, 4)))
            .attachedTags(AttachedTags.of(List.of("액티비티")))
            .ownerId(3L)
            .build();
}
