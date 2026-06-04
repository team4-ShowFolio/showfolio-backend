package com.example.showfolio.exception;

// 당일 토큰 할당량 한도 초과 시 발생
public class DailyLimitExceededException extends RuntimeException {

    private final int todayUsed;
    private final int dailyLimit;

    public DailyLimitExceededException(int todayUsed, int dailyLimit) {
        super(String.format(
                "당일 토큰 할당량 한도를 초과했습니다. (사용: %d / 한도: %d)",
                todayUsed, dailyLimit));
        this.todayUsed = todayUsed;
        this.dailyLimit = dailyLimit;
    }

    public int getTodayUsed() { return todayUsed; }
    public int getDailyLimit() { return dailyLimit; }
}
