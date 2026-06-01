package com.example.showfolio.dto;

import com.example.showfolio.entity.ProcessStatus;
import jakarta.validation.constraints.NotNull;

public record ReportProcessRequest(
        // TODO 인증 서비스 완료되면 수정 필요ㅑ
        @NotNull(message = "관리자 ID는 필수입니다.")
        Long adminId,

        @NotNull(message = "처리 상태는 필수입니다.")
        ProcessStatus status,

        String reason
) {
}
