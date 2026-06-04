package com.example.showfolio.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateProfileRequest {
    private String nickname;
    private String profileImage;
    private String bio;
}