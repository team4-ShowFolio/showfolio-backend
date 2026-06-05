package com.example.showfolio.dto;

import com.example.showfolio.enums.MemberStatus;
import com.example.showfolio.enums.Role;

public record MemberSearchCondition(
        String keyword,
        Role role,
        MemberStatus status
) {
}
