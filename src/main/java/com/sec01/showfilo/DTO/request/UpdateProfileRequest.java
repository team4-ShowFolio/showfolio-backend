package com.sec01.showfilo.DTO.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateProfileRequest {
    private String nickname;
    private String bio;
    private String profileImage;
}
