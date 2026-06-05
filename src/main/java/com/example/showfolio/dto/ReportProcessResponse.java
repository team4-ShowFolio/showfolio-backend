package com.example.showfolio.dto;

import com.example.showfolio.entity.ProcessStatus;
import com.example.showfolio.entity.ReportProcess;

import java.time.Instant;

public record ReportProcessResponse(
        Long id,
        Long reportId,
        Long adminId,
        ProcessStatus status,
        String reason,
        Instant createdAt,
        Instant updatedAt
) {
    public static ReportProcessResponse from(ReportProcess reportProcess) {
        return new ReportProcessResponse(
                reportProcess.getId(),
                reportProcess.getReport().getId(),
                reportProcess.getAdminId(),
                reportProcess.getStatus(),
                reportProcess.getReason(),
                reportProcess.getCreatedAt(),
                reportProcess.getUpdatedAt()
        );
    }
}
