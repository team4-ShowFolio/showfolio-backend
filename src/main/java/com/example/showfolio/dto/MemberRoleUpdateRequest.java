package com.example.showfolio.dto;

import jakarta.validation.constraints.NotNull;

public record MemberRoleUpdateRequest(
        // TODO Member 기능 병합 후 MemberRole 타입으로 변경
        @NotNull(message = "권한값은 필수입니다.")
        String role
) {
}
