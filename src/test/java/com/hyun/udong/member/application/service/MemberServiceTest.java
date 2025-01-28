package com.hyun.udong.member.application.service;

import com.hyun.udong.member.domain.Member;
import com.hyun.udong.member.domain.SocialType;
import com.hyun.udong.member.exception.MemberNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        memberService.save(new Member(1L, SocialType.KAKAO, "짱구", "https://user1.com"));
        memberService.save(new Member(2L, SocialType.KAKAO, "짱아", "https://user2.com"));
    }

    @Test
    void 신규_사용자일_경우_사용자_정보를_저장한다() {
        Member member = new Member(3L, SocialType.KAKAO, "신영만", "https://user3.com");

        Member savedMember = memberService.save(member);

        assertAll(
                () -> assertThat(member.getSocialId()).isEqualTo(savedMember.getSocialId()),
                () -> assertThat(member.getNickname()).isEqualTo("신영만"),
                () -> assertThat(member.getProfileImageUrl()).isEqualTo("https://user3.com")
        );
    }

    @Test
    void 기존_사용자일_경우_사용자_정보를_반환한다() {
        Member member = new Member(2L, SocialType.KAKAO, "짱아", "https://user2.com");

        Member savedMember = memberService.save(member);

        assertAll(
                () -> assertThat(member.getSocialId()).isEqualTo(savedMember.getSocialId()),
                () -> assertThat(member.getNickname()).isEqualTo(savedMember.getNickname()),
                () -> assertThat(member.getProfileImageUrl()).isEqualTo(savedMember.getProfileImageUrl())
        );
    }

    @Test
    void id로_사용자_정보를_조회했을_때_없는_사용자일_경우_예외를_발생시킨다() {
        assertThrows(MemberNotFoundException.class, () -> memberService.findById(100L));
    }
}
