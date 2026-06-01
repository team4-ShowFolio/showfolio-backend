package com.example.showfolio.dto;

import com.example.showfolio.entity.MemberStatus;

public record MemberSearchCondition(
        // TODO Member 기능 병합 후 MemberRole 타입으로 변경
        String role,
        MemberStatus status
) {
}
