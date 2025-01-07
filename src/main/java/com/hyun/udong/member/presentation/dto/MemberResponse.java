package com.hyun.udong.member.presentation.dto;

import com.hyun.udong.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MemberResponse {
    private Long id;
    private String nickname;
    private String gender;
    private int age;
    private String profileImageUrl;

    public static MemberResponse from(Member member) {
        return new MemberResponse(member.getId(), member.getNickname(), member.getGender(), member.getAge(), member.getProfileImageUrl());
    }
}
