package com.example.showfolio.dto.response;

import com.example.showfolio.entity.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class CommentResponse {

    private Long id;
    private Long feedId;
    private Long userId;
    private String userNickname;
    private String userProfileImage;
    private Long parentId;
    private String content;
    private List<CommentResponse> replies;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CommentResponse from(Comment comment, List<CommentResponse> replies) {
        return CommentResponse.builder()
                .id(comment.getId())
                .feedId(comment.getFeed().getId())
                .userId(comment.getMember().getId())
                .userNickname(comment.getMember().getNickname())
                .userProfileImage(comment.getMember().getProfileImage())
                .parentId(comment.getParent() != null
                        ? comment.getParent().getId()
                        : null)
                .content(comment.getContent())
                .replies(replies)
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
