package com.example.showfolio.dto;

import com.example.showfolio.entity.ReportReason;
import com.example.showfolio.entity.TargetType;

public record ReportSearchCondition(
        Long targetId,
        Long userId,
        TargetType targetType,
        ReportReason reportReason
) {
}
