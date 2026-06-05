package com.example.showfolio.component;

import com.example.showfolio.entity.ReportReason;
import com.example.showfolio.entity.TargetType;
import com.example.showfolio.port.ReportValidator;
import com.example.showfolio.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportValidatorService implements ReportValidator {

    private final ReportRepository reportRepository;

    // TODO Feed, Comment 기능 병합하면 그때 구현 진행
    @Override
    public void validateTargetExists(Long targetId, TargetType targetType) {
        switch (targetType) {
            case FEED -> {
                // TODO: FeedRepository 주입 후 구현
            }
            case COMMENT -> {
                // TODO: CommentRepository 주입 후 구현
            }
        }
    }

    @Override
    public void validateDuplicateReport(Long userId, Long targetId, TargetType targetType) {
        if (reportRepository.existsByReporterIdAndTargetIdAndTargetType(userId, targetId, targetType)) {
            // TODO GlobalExceptionHandler 구현 완료되면 확인 필요
            throw new IllegalStateException("이미 신고한 대상입니다.");
        }
    }

    // TODO Feed, Comment 기능 병합하면 그때 구현 진행
    @Override
    public void validateSelfReport(Long userId, Long targetId, TargetType targetType) {
        Long authorId = switch (targetType) {
            case FEED -> null; // TODO: FeedRepository 주입 후 작성자 ID 조회
            case COMMENT -> null; // TODO: CommentRepository 주입 후 작성자 ID 조회
        };

        if (userId.equals(authorId)) {
            throw new IllegalArgumentException("자신의 게시글은 신고할 수 없습니다.");
        }
    }

    @Override
    public void validateReportReason(ReportReason reason, String content) {
        if (reason == ReportReason.OTHER && (content == null || content.isBlank())) {
            throw new IllegalArgumentException("기타 사유 선택 시 내용을 입력해야 합니다.");
        }
    }
}
