package com.example.showfolio.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FeedLikeResponse {

    private Long feedId;
    private int likeCount;
    private boolean isLiked;

    public static FeedLikeResponse of(Long feedId, int likeCount, boolean isLiked) {
        return FeedLikeResponse.builder()
                .feedId(feedId)
                .likeCount(likeCount)
                .isLiked(isLiked)
                .build();
    }
}
