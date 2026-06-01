package com.example.showfolio.project.dto;

// liked: 이번 토글 결과(true=좋아요됨, false=취소됨), likeCount: 현재 좋아요 수
public record ProjectLikeResponse(
        boolean liked,
        int likeCount
) {}
