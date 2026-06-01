package com.example.showfolio.controller;

import com.example.showfolio.dto.request.FeedCreateRequest;
import com.example.showfolio.dto.request.FeedUpdateRequest;
import com.example.showfolio.dto.response.FeedLikeResponse;
import com.example.showfolio.dto.response.FeedResponse;
import com.example.showfolio.service.FeedService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feeds")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    // 피드 작성
    @PostMapping
    public ResponseEntity<FeedResponse> createFeed(
            @Valid @RequestBody FeedCreateRequest request,

            // JWT 완성 후 @AuthenticationPrincipal Long currentUserId로 교체
            @RequestHeader("X-User-Id") Long currentUserId) {

        return ResponseEntity.ok(feedService.createFeed(request, currentUserId));
    }

    // 피드 단건 조회
    @GetMapping("/{feedId}")
    public ResponseEntity<FeedResponse> getFeed(
            @PathVariable Long feedId,

            // JWT 완성 후 @AuthenticationPrincipal Long currentUserId로 교체
            @RequestHeader("X-User-Id") Long currentUserId) {

        return ResponseEntity.ok(feedService.getFeed(feedId, currentUserId));
    }

    // 피드 수정
    @PutMapping("/{feedId}")
    public ResponseEntity<FeedResponse> updateFeed(
            @PathVariable Long feedId,
            @Valid @RequestBody FeedUpdateRequest request,

            // JWT 완성 후 @AuthenticationPrincipal Long currentUserId로 교체
            @RequestHeader("X-User-Id") Long currentUserId) {

        return ResponseEntity.ok(feedService.updateFeed(feedId, request, currentUserId));
    }

    // 피드 삭제
    @DeleteMapping("/{feedId}")
    public ResponseEntity<Void> deleteFeed(
            @PathVariable Long feedId,

            // JWT 완성 후 @AuthenticationPrincipal Long currentUserId로 교체
            @RequestHeader("X-User-Id") Long currentUserId) {

        feedService.deleteFeed(feedId, currentUserId);
        return ResponseEntity.noContent().build();
    }

    // 전체 피드 (최신순)
    @GetMapping
    public ResponseEntity<Page<FeedResponse>> getFeeds(
            @RequestParam(defaultValue = "0") int page,

            // JWT 완성 후 @AuthenticationPrincipal Long currentUserId로 교체
            @RequestHeader("X-User-Id") Long currentUserId) {

        return ResponseEntity.ok(feedService.getFeeds(page, currentUserId));
    }

    // 팔로잉 피드
    @GetMapping("/following")
    public ResponseEntity<Page<FeedResponse>> getFollowingFeeds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam List<Long> followingIds,

            // JWT 완성 후 @AuthenticationPrincipal Long currentUserId로 교체
            @RequestHeader("X-User-Id") Long currentUserId) {

        return ResponseEntity.ok(
                feedService.getFollowingFeeds(followingIds, page, currentUserId));
    }

    // 내 피드
    @GetMapping("/me")
    public ResponseEntity<Page<FeedResponse>> getMyFeeds(
            @RequestParam(defaultValue = "0") int page,

            // JWT 완성 후 @AuthenticationPrincipal Long currentUserId로 교체
            @RequestHeader("X-User-Id") Long currentUserId) {

        return ResponseEntity.ok(feedService.getMyFeeds(page, currentUserId));
    }

    // 좋아요 토글
    @PostMapping("/{feedId}/likes")
    public ResponseEntity<FeedLikeResponse> toggleLike(
            @PathVariable Long feedId,

            // JWT 완성 후 @AuthenticationPrincipal Long currentUserId로 교체
            @RequestHeader("X-User-Id") Long currentUserId) {

        return ResponseEntity.ok(feedService.toggleLike(feedId, currentUserId));
    }
}
