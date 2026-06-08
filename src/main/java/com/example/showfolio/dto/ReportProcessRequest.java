package com.example.showfolio.dto;

import com.example.showfolio.entity.ProcessStatus;
import jakarta.validation.constraints.NotNull;

public record ReportProcessRequest(
        @NotNull(message = "처리 상태는 필수입니다.")
        ProcessStatus status,

        String reason
) {
}
