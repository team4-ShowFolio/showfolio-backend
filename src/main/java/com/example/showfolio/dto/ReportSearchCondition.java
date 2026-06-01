package com.example.showfolio.dto;

import com.example.showfolio.entity.TargetType;

public record ReportSearchCondition(
        TargetType targetType,
        Long reporterId
) {
}
