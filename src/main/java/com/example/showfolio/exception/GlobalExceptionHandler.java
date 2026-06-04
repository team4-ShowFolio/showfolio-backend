package com.example.showfolio.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // @Valid 검증 실패 - 첫 번째 에러 메시지만 반환
    // 400 Bad Request + Body(ErrorResponse)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("입력값이 올바르지 않습니다.");

        log.warn("검증 실패: {}", message);
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(message));
    }

    // JSON 자체가 깨짐
    // 400 Bad Request + Body(ErrorResponse)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMalformedJson(
            HttpMessageNotReadableException ex) {

        log.warn("JSON 파싱 실패: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("요청 형식이 올바르지 않습니다."));
    }

    // 무료회원이 유료구독 기능에 접근
    // 403 Bad Request + Body(ErrorResponse)
    @ExceptionHandler(PremiumRequiredException.class)
    public ResponseEntity<ErrorResponse> handlePremiumRequired(
            PremiumRequiredException ex) {

        log.info("유료 기능 접근 거부: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)  // 403
                .body(new ErrorResponse(ex.getMessage()));
    }

    // 당일 토큰 할당량 한도 초과
    // 429 Bad Request + Body(ErrorResponse)
    @ExceptionHandler(DailyLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleDailyLimit(
            DailyLimitExceededException ex) {

        log.info("당일 토큰 할당량 한도 초과: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)  // 429
                .body(new ErrorResponse(ex.getMessage()));
    }

    // 당월 토큰 할당량 한도 초과
    // 429 Bad Request + Body(ErrorResponse)
    @ExceptionHandler(MonthlyLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleMonthlyLimit(
            MonthlyLimitExceededException ex) {

        log.info("당월 토큰 할당량 한도 초과: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)  // 429
                .body(new ErrorResponse(ex.getMessage()));
    }

    // 회원 조회 실패
    // 404 Not Found + Body(ErrorResponse)
    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMemberNotFound(MemberNotFoundException ex) {
        log.warn("회원 조회 실패: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND) // 404
                .body(new ErrorResponse(ex.getMessage()));
    }

    // 등록된 프로젝트가 없거나 특정 프로젝트를 찾을 수 없음
    // 404 Not Found + Body(ErrorResponse)
    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProjectNotFound(
            ProjectNotFoundException ex) {

        log.warn("프로젝트 조회 실패: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND) // 404
                .body(new ErrorResponse(ex.getMessage()));
    }

    // 본인 소유의 프로젝트가 아님 (권한 부족)
    // 403 Forbidden + Body(ErrorResponse)
    @ExceptionHandler(ProjectAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleProjectAccessDenied(
            ProjectAccessDeniedException ex) {

        log.warn("프로젝트 접근 권한 없음: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN) // 403
                .body(new ErrorResponse(ex.getMessage()));
    }

    // AI 응답이 규격에 맞지 않거나 필수 항목이 누락된 경우
    // 502 Bad Gateway + Body(ErrorResponse)
    // 외부(AI) 응답 문제 → 재시도 유도
    @ExceptionHandler(AiResponseFormatException.class)
    public ResponseEntity<ErrorResponse> handleAiResponseFormat(AiResponseFormatException ex) {

        log.warn("AI 응답 규격 오류: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY) // 502
                .body(new ErrorResponse(ex.getMessage()));
    }

    /**
     * 그 외 모든 예외 (DB 오류, NPE 등)
     */
    // 500 Internal Server Error + Body(ErrorResponse)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex) {

        log.error("예외 발생", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("서버에서 오류가 발생했습니다. 잠시 후 다시 시도해주세요."));
    }
}