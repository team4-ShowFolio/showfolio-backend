package com.example.showfolio.dto;

import com.example.showfolio.entity.Report;
import com.example.showfolio.entity.ReportReason;
import com.example.showfolio.entity.TargetType;

import java.time.Instant;

public record ReportResponse(
        Long id,
        Long userId,
        Long targetId,
        TargetType targetType,
        ReportReason reportReason,
        String content,
        Instant createdAt
) {
    public static ReportResponse from(Report report) {
        return new ReportResponse(
                report.getId(),
                report.getReporterId(),
                report.getTargetId(),
                report.getTargetType(),
                report.getReportReason(),
                report.getContent(),
                report.getCreatedAt()
        );
    }
}
