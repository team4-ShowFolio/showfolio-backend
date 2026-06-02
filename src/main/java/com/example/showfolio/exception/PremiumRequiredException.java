package com.example.showfolio.exception;

// 유료 구독이 필요한 기능에 무료 회원이 접근 시 발생
public class PremiumRequiredException extends RuntimeException {
    public PremiumRequiredException() {
        super("유료 구독자만 이용 가능한 기능입니다.");
    }
}