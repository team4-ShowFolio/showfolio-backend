package com.example.showfolio.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// AI 기반 {프로젝트 설명 개선} API의 요청 객체(DTO).
// 리액트 프론트엔드의 텍스트 입력창(textarea) 내용을 그대로 전달받아 검증.
public record DescriptionImproveRequest(

        @NotBlank(message = "프로젝트 설명은 필수 항목입니다.") // 공백, null 방지
        @Size(max = 5000, message = "설명은 최대 5000자까지 입력 가능합니다.") // DB TEXT 타입 오버플로우 방지
        String description
) {}
