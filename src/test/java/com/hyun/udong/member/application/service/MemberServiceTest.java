package com.hyun.udong.member.application.service;

import com.hyun.udong.member.domain.Member;
import com.hyun.udong.member.domain.SocialType;
import com.hyun.udong.member.exception.MemberNotFoundException;
import com.hyun.udong.member.presentation.dto.MemberResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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

        MemberResponse savedMember = memberService.save(member);

        MemberResponse findMember = memberService.getMemberById(savedMember.getId());
        assertAll(
                () -> assertThat(findMember.getId()).isEqualTo(savedMember.getId()),
                () -> assertThat(findMember.getNickname()).isEqualTo("신영만"),
                () -> assertThat(findMember.getProfileImageUrl()).isEqualTo("https://user3.com")
        );
    }

    @Test
    @DisplayName("기존 사용자일 경우 사용자 정보를 반환한다.")
    void updateMember() {
        Member member = new Member(2L, SocialType.KAKAO, "짱아", "https://user2.com");

        MemberResponse updatedMember = memberService.save(member);

        MemberResponse findMember = memberService.getMemberById(updatedMember.getId());
        assertAll(
                () -> assertThat(findMember.getId()).isEqualTo(updatedMember.getId()),
                () -> assertThat(findMember.getNickname()).isEqualTo(updatedMember.getNickname()),
                () -> assertThat(findMember.getProfileImageUrl()).isEqualTo(updatedMember.getProfileImageUrl())
        );
    }

    @Test
    @DisplayName("id로 사용자 정보를 조회했을 때 없는 사용자일 경우 예외를 발생시킨다.")
    void getMemberByIdWithNotExists() {
        assertThrows(MemberNotFoundException.class, () -> memberService.getMemberById(100L));
    }

    @Test
    @DisplayName("없는 refresh token으로 사용자 정보를 조회했을 때 예외를 발생시킨다.")
    void findByRefreshTokenWithNotExists() {
        assertThrows(MemberNotFoundException.class, () -> memberService.findByRefreshToken(""));
    }

    @Test
    @DisplayName("refresh token을 수정한다.")
    void updateRefreshToken() {
        Member member = memberService.save2(new Member(3L, SocialType.KAKAO, "신영만", "https://user3.com"));
        Member updatedMember = memberService.updateRefreshToken(member.getId(), "refreshToken");

        assertEquals("refreshToken", updatedMember.getRefreshToken());
    }
}
