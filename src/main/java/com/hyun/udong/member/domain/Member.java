package com.hyun.udong.member.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long socialId;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @With
    private String nickname;

    private String gender;

    private int age;

    @With
    private String profileImageUrl;

    public Member(Long socialId, SocialType socialType, String nickname, String profileImageUrl) {
        this.socialId = socialId;
        this.socialType = socialType;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }
}
