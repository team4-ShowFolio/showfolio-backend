package com.example.showfolio.dto;

import jakarta.validation.constraints.NotNull;

public record MemberSuspendRequest(
        // TODO Member 기능 병합 후 MemberStatus 타입으로 변경
        @NotNull(message = "상태값은 필수입니다.")
        String status
) {
}
