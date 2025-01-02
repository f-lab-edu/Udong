package com.hyun.udong.auth.presentation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hyun.udong.member.domain.Member;
import com.hyun.udong.member.domain.SocialType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoProfileResponse {

    //회원 번호
    @JsonProperty("id")
    private Long id;

    private String nickname;

    private String profileImageUrl;

    @SuppressWarnings("unchecked")
    @JsonProperty("kakao_account")
    private void unpackNested(Map<String, Object> kakaoAccount) {
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        this.nickname = (String) profile.get("nickname");
        this.profileImageUrl = (String) profile.get("profile_image_url");
    }

    public Member toMember() {
        return new Member(id, SocialType.KAKAO, getNickname(), getProfileImageUrl());
    }
}
