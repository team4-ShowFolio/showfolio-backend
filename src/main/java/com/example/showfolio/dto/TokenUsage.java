package com.example.showfolio.dto;

// AI 토큰 사용량 정보 (모든 AI API 응답에서 공통 사용).
public class TokenUsage {

    public record Daily(
            int todayUsed,         // 당일 총 소모 토큰량 (Input + Output 누적치)
            int dailyLimit,        // 당일 한도 토큰량
            int remaining          // 당일 잔여 토큰량 (dailyLimit - todayUsed)
    ) {
    }

    public record Monthly(
            int monthUsed,          // 당월 총 소모 토큰량 (Input + Output 누적치)
            int monthlyLimit,       // 당월 한도 토큰량
            int remaining           // 당월 잔여 토큰량 (dailyLimit - todayUsed)
    ) {
    }
}
