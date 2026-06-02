package com.example.showfolio.dto;

// AI 기반 {이력서용 문장 변환} API 응답 DTO.
// 변환된 이력서 문장 + 유저별 AI 토큰 사용량(당일,당월) 정보
public record ResumeConvertResponse(
        String resumeText,          // 변환된 이력서 문장
        TokenUsage.Daily daily,     // 유저별 AI 토큰 사용량(당일) 정보
        TokenUsage.Monthly monthly  // 유저별 AI 토큰 사용량(당월) 정보
) {}
