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

    public static FeedResponse from(Feed feed, boolean isLiked) {
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
                .commentCount(feed.getComments().size())
                .isLiked(isLiked)
                .tags(feed.getTags().stream()
                        .map(tag -> tag.getTagName())
                        .toList())
                .imageUrls(feed.getImages().stream()
                        .sorted((a, b) -> a.getOrderNum() - b.getOrderNum())
                        .map(image -> image.getImageUrl())
                        .toList())
                .mentionedUserIds(feed.getMentions().stream()
                        .map(mention -> mention.getMentionedUser().getId())
                        .toList())
                .createdAt(feed.getCreatedAt())
                .updatedAt(feed.getUpdatedAt())
                .build();
    }
}
