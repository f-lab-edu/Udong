package com.hyun.udong.member.domain;

import com.hyun.udong.common.entity.BaseTimeEntity;
import com.hyun.udong.travelschedule.domain.MemberTravelSchedule;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private Long socialId;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    private String nickname;

    private String gender;

    private int age;

    private String profileImageUrl;

    private String refreshToken;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private MemberTravelSchedule travelSchedule;

    public Member(Long socialId, SocialType socialType, String nickname, String profileImageUrl) {
        this.socialId = socialId;
        this.socialType = socialType;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }

    public static Member createForTest(Long id, String nickname) {
        Member member = new Member();
        member.id = id;
        member.nickname = nickname;
        return member;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void registerTravelSchedule(MemberTravelSchedule travelSchedule) {
        this.travelSchedule = travelSchedule;
    }
}
