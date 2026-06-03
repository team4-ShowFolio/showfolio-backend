package com.example.showfolio.dto;

import com.example.showfolio.mock.Role;
import jakarta.validation.constraints.NotNull;

public record MemberRoleUpdateRequest(
        @NotNull(message = "권한값은 필수입니다.")
        Role role
) {
}
