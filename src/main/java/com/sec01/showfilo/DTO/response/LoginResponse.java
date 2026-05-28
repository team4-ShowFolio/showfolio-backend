package com.sec01.showfilo.DTO.response;

import com.sec01.showfilo.entity.User;
import lombok.Getter;

@Getter
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private UserResponse user;

    public LoginResponse(String accessToken, String refreshToken, User user){
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.user = new UserResponse(user);
    }
}
