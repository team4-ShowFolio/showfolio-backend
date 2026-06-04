package com.example.showfolio.controller;

import com.example.showfolio.DTO.response.FollowCountResponse;
import com.example.showfolio.DTO.response.FollowResponse;
import com.example.showfolio.service.FollowService;
import com.example.showfolio.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;
    private final JwtUtil jwtUtil;

    // 팔로우
    @PostMapping("/{followingId}")
    public ResponseEntity<?> follow(
            @RequestHeader("Authorization") String token,
            @PathVariable Long followingId) {
        Long followerId = jwtUtil.getUserId(token.substring(7));
        followService.follow(followerId, followingId);
        return ResponseEntity.ok("팔로우 성공");
    }

    // 언팔로우
    @DeleteMapping("/{followingId}")
    public ResponseEntity<?> unfollow(
            @RequestHeader("Authorization") String token,
            @PathVariable Long followingId) {
        Long followerId = jwtUtil.getUserId(token.substring(7));
        followService.unfollow(followerId, followingId);
        return ResponseEntity.ok("언팔로우 성공");
    }

    // 내 팔로워 목록
    @GetMapping("/followers")
    public ResponseEntity<List<FollowResponse>> getFollowers(
            @RequestHeader("Authorization") String token) {
        Long memberId = jwtUtil.getUserId(token.substring(7));
        return ResponseEntity.ok(followService.getFollowers(memberId));
    }

    // 내 팔로잉 목록
    @GetMapping("/followings")
    public ResponseEntity<List<FollowResponse>> getFollowings(
            @RequestHeader("Authorization") String token) {
        Long memberId = jwtUtil.getUserId(token.substring(7));
        return ResponseEntity.ok(followService.getFollowings(memberId));
    }

    // 팔로워 수 + 팔로잉 수
    @GetMapping("/count")
    public ResponseEntity<FollowCountResponse> getFollowCount(
            @RequestHeader("Authorization") String token) {
        Long memberId = jwtUtil.getUserId(token.substring(7));
        return ResponseEntity.ok(followService.getFollowCount(memberId));
    }

    // 팔로우 여부 확인
    @GetMapping("/check/{followingId}")
    public ResponseEntity<?> isFollowing(
            @RequestHeader("Authorization") String token,
            @PathVariable Long followingId) {
        Long followerId = jwtUtil.getUserId(token.substring(7));
        return ResponseEntity.ok(followService.isFollowing(followerId, followingId));
    }
}