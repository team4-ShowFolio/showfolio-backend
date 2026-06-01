package com.example.showfolio.port;

import com.example.showfolio.entity.ReportReason;
import com.example.showfolio.entity.TargetType;

/**
 * 신고 관련해서 데이터 검증을 위한 인터페이스 입니다.
 *
 */
public interface ReportValidator {
    void validateTargetExists(Long targetId, TargetType targetType);

    void validateDuplicateReport(Long reporterId, Long targetId, TargetType targetType);

    void validateSelfReport(Long reporterId, Long targetId, TargetType targetType);

    void validateReportReason(ReportReason reason, String content);
}
