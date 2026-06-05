package com.example.showfolio.dto.response;

import com.example.showfolio.entity.Member;
import lombok.Getter;

@Getter
public class FollowResponse {
    private Long memberId;
    private String nickname;
    private String profileImage;
    private String bio;

    public FollowResponse(Member member) {
        this.memberId = member.getId();
        this.nickname = member.getNickname();
        this.profileImage = member.getProfileImage();
        this.bio = member.getBio();
    }
}