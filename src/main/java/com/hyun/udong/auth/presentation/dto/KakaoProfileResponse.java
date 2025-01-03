package com.hyun.udong.auth.presentation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hyun.udong.member.domain.Member;
import com.hyun.udong.member.domain.SocialType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoProfileResponse {

    private Long id;

    private KakaoAccount kakaoAccount;

    @Getter
    public static class KakaoAccount {

        private Profile profile;

        @Getter
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        private static class Profile {

            private String nickname;
            private String profileImageUrl;
        }

        public String getNickname() {
            return profile.getNickname();
        }

        public String getProfileImageUrl() {
            return profile.getProfileImageUrl();
        }
    }

    public Member toMember() {
        KakaoAccount account = getKakaoAccount();
        return new Member(id, SocialType.KAKAO, account.getNickname(), account.getProfileImageUrl());
    }
}
