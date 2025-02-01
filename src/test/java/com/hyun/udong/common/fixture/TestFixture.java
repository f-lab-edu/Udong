package com.hyun.udong.common.fixture;

import com.hyun.udong.member.domain.Member;
import com.hyun.udong.member.domain.SocialType;

public class TestFixture {
    public static final Member HYUN = new Member(1L, SocialType.KAKAO, "hyun", "profile_image");
}
