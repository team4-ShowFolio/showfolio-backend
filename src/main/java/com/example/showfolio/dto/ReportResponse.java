package com.example.showfolio.dto;

import com.example.showfolio.entity.Report;
import com.example.showfolio.entity.ReportReason;
import com.example.showfolio.entity.TargetType;

import java.time.Instant;
// TODO 신고자 닉네임, 피신고자 닉네임, 신고사유, 게시글 ID, 게시글 타이틀
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
                report.getUserId(),
                report.getTargetId(),
                report.getTargetType(),
                report.getReportReason(),
                report.getContent(),
                report.getCreatedAt()
        );
    }
}
