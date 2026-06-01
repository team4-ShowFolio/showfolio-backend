package com.example.showfolio.dto;

import com.example.showfolio.entity.ReportReason;
import com.example.showfolio.entity.TargetType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReportCreateRequest(
        // TODO 이후 인증 기능 적용 시 삭제 필요
        @NotNull(message = "신고자 ID는 필수입니다.")
        Long userId,

        @NotNull(message = "신고 대상 ID는 필수입니다.")
        Long targetId,

        @NotNull(message = "신고 대상 타입은 필수입니다.")
        TargetType targetType,

        @NotNull(message = "신고 사유는 필수입니다.")
        ReportReason reportReason,

        @Size(max = 500, message = "신고 내용은 500자를 초과할 수 없습니다.")
        String content
) {
}
