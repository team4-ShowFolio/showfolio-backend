package com.example.showfolio.DTO.response;

import com.example.showfolio.entity.Member;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class MemberResponse {
    private Long id;
    private String email;
    private String nickname;
    private String profileImage;
    private String bio;
    private String role;
    private String subscriptionType;
    private LocalDateTime subscriptionExpiredAt;
    private LocalDateTime createdAt;

    public MemberResponse(Member member) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.nickname = member.getNickname();
        this.profileImage = member.getProfileImage();
        this.bio = member.getBio();
        this.role = member.getRole().name();
        this.subscriptionType = member.getSubscriptionType().name();
        this.subscriptionExpiredAt = member.getSubscriptionExpiredAt();
        this.createdAt = member.getCreatedAt();
    }
}