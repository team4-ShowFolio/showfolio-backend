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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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

            @AuthenticationPrincipal UserDetails userDetails) {
        Long currentUserId = Long.parseLong(userDetails.getUsername());

        return ResponseEntity.ok(feedService.createFeed(request, currentUserId));
    }

    // 피드 검색
    @GetMapping("/search")
    public ResponseEntity<Page<FeedResponse>> searchFeeds(
            @RequestParam(required = false) String keyword,

            @RequestParam(required = false) String tags,

            @RequestParam(required = false) String author,

            @RequestParam(defaultValue = "latest") String sort,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long currentUserId = Long.parseLong(userDetails.getUsername());

        return ResponseEntity.ok(
                feedService.searchFeeds(
                        keyword, tags, author, sort, page, size, currentUserId));
    }

    // 좋아요한 피드 목록
    @GetMapping("/liked")
    public ResponseEntity<Page<FeedResponse>> getLikedFeeds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,

            @AuthenticationPrincipal UserDetails userDetails) {
        Long currentUserId = Long.parseLong(userDetails.getUsername());

        return ResponseEntity.ok(
                feedService.getLikedFeeds(page, size, currentUserId));
    }

    // 팔로잉 피드
    @GetMapping("/following")
    public ResponseEntity<Page<FeedResponse>> getFollowingFeeds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) List<Long> followingIds,

            @AuthenticationPrincipal UserDetails userDetails) {
        Long currentUserId = Long.parseLong(userDetails.getUsername());

        // 팔로잉한 사람이 없으면 빈 목록 반환
        if (followingIds == null || followingIds.isEmpty()) {
            return ResponseEntity.ok(Page.empty());
        }

        return ResponseEntity.ok(
                feedService.getFollowingFeeds(followingIds, page, currentUserId));
    }

    // 내 피드
    @GetMapping("/me")
    public ResponseEntity<Page<FeedResponse>> getMyFeeds(
            @RequestParam(defaultValue = "0") int page,

            @AuthenticationPrincipal UserDetails userDetails) {
        Long currentUserId = Long.parseLong(userDetails.getUsername());

        return ResponseEntity.ok(feedService.getMyFeeds(page, currentUserId));
    }

    // 전체 피드 (최신순)
    @GetMapping
    public ResponseEntity<Page<FeedResponse>> getFeeds(
            @RequestParam(defaultValue = "0") int page,

            @AuthenticationPrincipal UserDetails userDetails) {
        Long currentUserId = Long.parseLong(userDetails.getUsername());

        return ResponseEntity.ok(feedService.getFeeds(page, currentUserId));
    }

    // 피드 단건 조회
    @GetMapping("/{feedId}")
    public ResponseEntity<FeedResponse> getFeed(
            @PathVariable Long feedId,

            @AuthenticationPrincipal UserDetails userDetails) {
        Long currentUserId = Long.parseLong(userDetails.getUsername());

        return ResponseEntity.ok(feedService.getFeed(feedId, currentUserId));
    }

    // 피드 수정
    @PutMapping("/{feedId}")
    public ResponseEntity<FeedResponse> updateFeed(
            @PathVariable Long feedId,
            @Valid @RequestBody FeedUpdateRequest request,

            @AuthenticationPrincipal UserDetails userDetails) {
        Long currentUserId = Long.parseLong(userDetails.getUsername());

        return ResponseEntity.ok(feedService.updateFeed(feedId, request, currentUserId));
    }

    // 피드 삭제
    @DeleteMapping("/{feedId}")
    public ResponseEntity<Void> deleteFeed(
            @PathVariable Long feedId,

            @AuthenticationPrincipal UserDetails userDetails) {
        Long currentUserId = Long.parseLong(userDetails.getUsername());
        feedService.deleteFeed(feedId, currentUserId);
        return ResponseEntity.noContent().build();
    }

    // 좋아요 토글
    @PostMapping("/{feedId}/likes")
    public ResponseEntity<FeedLikeResponse> toggleLike(
            @PathVariable Long feedId,

            @AuthenticationPrincipal UserDetails userDetails) {
        Long currentUserId = Long.parseLong(userDetails.getUsername());

        return ResponseEntity.ok(feedService.toggleLike(feedId, currentUserId));
    }
}
