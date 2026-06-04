package com.sec01.showfilo.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FollowCountResponse {
    private Long followerCount;   // 나를 팔로우하는 수
    private Long followingCount;  // 내가 팔로우하는 수
}