package com.example.showfolio.dto;

import com.example.showfolio.entity.ProcessStatus;

public record ReportProcessSearchCondition(
        Long reportId,
        Long reporterId,
        ProcessStatus status
) {
}
