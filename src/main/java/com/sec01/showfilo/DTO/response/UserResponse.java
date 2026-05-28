package com.sec01.showfilo.DTO.response;

import com.sec01.showfilo.entity.User;
import lombok.Getter;

@Getter
public class UserResponse {
    private Long id;
    private String email;
    private String nickname;
    private String profileImage;
    private String bio;

    public UserResponse(User user){
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.profileImage = user.getProfileImage();
        this.bio = user.getBio();
    }
}
