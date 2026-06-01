package com.example.showfolio.dto;

import java.time.Instant;

// TODO Member 기능 병합되면 필드 추가 필요
public record AdminMemberResponse(
        Long id,
        String nickname,
        int reportCount,
        Instant createdAt
) {
}
