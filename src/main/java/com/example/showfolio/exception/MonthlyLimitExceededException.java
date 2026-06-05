package com.example.showfolio.exception;

// 당월 토큰 할당량 한도 초과 시 발생
public class MonthlyLimitExceededException extends RuntimeException {

    private final int monthUsed;
    private final int monthlyLimit;

    public MonthlyLimitExceededException(int monthUsed, int monthlyLimit) {
        super(String.format(
                "당월 토큰 할당량 한도를 초과했습니다. (사용: %d / 한도: %d)",
                monthUsed, monthlyLimit));
        this.monthUsed = monthUsed;
        this.monthlyLimit = monthlyLimit;
    }

    public int getMonthUsed() { return monthUsed; }
    public int getMonthlyLimit() { return monthlyLimit; }
}
