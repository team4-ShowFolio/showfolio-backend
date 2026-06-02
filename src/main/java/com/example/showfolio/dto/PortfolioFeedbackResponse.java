package com.example.showfolio.dto;

// AI 기반 {포트폴리오 피드백} API의 응답 객체(DTO).
// 포트폴리오 피드백 +  유저별 AI 토큰 사용량(당일,당월) 정보
public record PortfolioFeedbackResponse(
        String feedback,            // AI가 생성한 피드백
        TokenUsage.Daily daily,     // 유저별 AI 토큰 사용량(당일) 정보
        TokenUsage.Monthly monthly  // 유저별 AI 토큰 사용량(당월) 정보
) {
}
