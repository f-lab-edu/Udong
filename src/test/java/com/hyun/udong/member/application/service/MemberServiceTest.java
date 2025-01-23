package com.hyun.udong.member.application.service;

import com.hyun.udong.member.domain.Member;
import com.hyun.udong.member.domain.SocialType;
import com.hyun.udong.member.exception.MemberNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        memberService.save(new Member(1L, SocialType.KAKAO, "짱구", "https://user1.com"));
        memberService.save(new Member(2L, SocialType.KAKAO, "짱아", "https://user2.com"));
    }

    @Test
    @DisplayName("신규 사용자일 경우 사용자 정보를 저장한다.")
    void saveMember() {
        Member member = new Member(3L, SocialType.KAKAO, "신영만", "https://user3.com");

        Member savedMember = memberService.save(member);

        assertAll(
                () -> assertThat(member.getSocialId()).isEqualTo(savedMember.getSocialId()),
                () -> assertThat(member.getNickname()).isEqualTo("신영만"),
                () -> assertThat(member.getProfileImageUrl()).isEqualTo("https://user3.com")
        );
    }

    @Test
    @DisplayName("기존 사용자일 경우 사용자 정보를 반환한다.")
    void updateMember() {
        Member member = new Member(2L, SocialType.KAKAO, "짱아", "https://user2.com");

        Member savedMember = memberService.save(member);

        assertAll(
                () -> assertThat(member.getSocialId()).isEqualTo(savedMember.getSocialId()),
                () -> assertThat(member.getNickname()).isEqualTo(savedMember.getNickname()),
                () -> assertThat(member.getProfileImageUrl()).isEqualTo(savedMember.getProfileImageUrl())
        );
    }

    @Test
    @DisplayName("id로 사용자 정보를 조회했을 때 없는 사용자일 경우 예외를 발생시킨다.")
    void getMemberByIdWithNotExists() {
        assertThrows(MemberNotFoundException.class, () -> memberService.findById(100L));
    }
}
