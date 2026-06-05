package com.example.showfolio.dto;

import com.example.showfolio.enums.MemberStatus;
import jakarta.validation.constraints.NotNull;

public record MemberSuspendRequest(
        @NotNull(message = "상태값은 필수입니다.")
        MemberStatus status
) {
}
