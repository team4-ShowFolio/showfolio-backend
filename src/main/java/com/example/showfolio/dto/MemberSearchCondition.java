package com.example.showfolio.dto;

public record MemberSearchCondition(
        String keyword,
        // TODO Member 기능 병합 후 MemberRole 타입으로 변경
        String role,
        // TODO Member 기능 병합 후 MemberStatus 타입으로 변경
        String status
) {
}
