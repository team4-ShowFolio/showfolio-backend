package com.example.showfolio.exception;

// AI 응답이 규격에 맞지 않거나 필수 항목이 누락된 경우
public class AiResponseFormatException extends RuntimeException {
    public AiResponseFormatException() {
        super("AI 응답 생성에 일시적 문제, 다시 시도해 주세요.");
    }
}
