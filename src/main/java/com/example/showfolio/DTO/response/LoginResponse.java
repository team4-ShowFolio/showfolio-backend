package com.example.showfolio.DTO.response;

import com.example.showfolio.entity.Member;
import lombok.Getter;

@Getter
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private MemberResponse member;

    public LoginResponse(String accessToken, String refreshToken, Member member) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.member = new MemberResponse(member);
    }
}