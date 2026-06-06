package com.example.showfolio.dto.response;

import com.example.showfolio.dto.ProjectResponse;
import com.example.showfolio.entity.Feed;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class FeedResponse {

    private Long id;
    private Long userId;
    private String userNickname;
    private String userProfileImage;
    private String title;
    private String content;
    private String visibility;
    private ProjectResponse project;
    private int likeCount;
    private int commentCount;
    private boolean isLiked;
    private List<String> tags;
    private List<String> imageUrls;
    private List<Long> mentionedUserIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static FeedResponse from(Feed feed, boolean isLiked, int commentCount,
                                    List<String> tags, List<String> imageUrls, List<Long> mentionedUserIds) {
        return FeedResponse.builder()
                .id(feed.getId())
                .userId(feed.getMember().getId())
                .userNickname(feed.getMember().getNickname())
                .userProfileImage(feed.getMember().getProfileImage())
                .title(feed.getTitle())
                .content(feed.getContent())
                .visibility(feed.getVisibility().name())
                .project(feed.getProject() != null ? ProjectResponse.from(feed.getProject()) : null)
                .likeCount(feed.getLikeCount())
                .commentCount(commentCount)
                .isLiked(isLiked)
                .tags(tags)
                .imageUrls(imageUrls)
                .mentionedUserIds(mentionedUserIds)
                .createdAt(feed.getCreatedAt())
                .updatedAt(feed.getUpdatedAt())
                .build();
    }

    // 기존 호환성 유지 (다른 곳에서 from(feed, isLiked) 쓰는 곳)
    public static FeedResponse from(Feed feed, boolean isLiked, int commentCount, List<Long> mentionedUserIds) {
        return from(feed, isLiked, commentCount, List.of(), List.of(), mentionedUserIds);
    }

    public static FeedResponse from(Feed feed, boolean isLiked, int commentCount) {
        return from(feed, isLiked, commentCount, List.of(), List.of(), List.of());
    }

    public static FeedResponse from(Feed feed, boolean isLiked) {
        return from(feed, isLiked, 0, List.of(), List.of(), List.of());
    }
}
