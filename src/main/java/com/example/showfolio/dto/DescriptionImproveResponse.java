package com.example.showfolio.dto;

// AI 기반 {프로젝트 설명 개선} API의 응답 객체(DTO).
// 개선된 프로젝트 설명 +  유저별 AI 토큰 사용량(당일,당월) 정보
public record DescriptionImproveResponse(
        String improvedDescription,   // 개선된 프로젝트 설명
        TokenUsage.Daily daily,       // 유저별 AI 토큰 사용량(당일) 정보
        TokenUsage.Monthly monthly    // 유저별 AI 토큰 사용량(당월) 정보
) {}