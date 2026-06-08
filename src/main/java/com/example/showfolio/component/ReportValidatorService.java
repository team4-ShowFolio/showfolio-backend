package com.example.showfolio.component;

import com.example.showfolio.entity.ReportReason;
import com.example.showfolio.entity.TargetType;
import com.example.showfolio.port.ReportValidator;
import com.example.showfolio.repository.CommentRepository;
import com.example.showfolio.repository.FeedRepository;
import com.example.showfolio.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportValidatorService implements ReportValidator {

    private final ReportRepository reportRepository;
    private final FeedRepository feedRepository;
    private final CommentRepository commentRepository;

    @Override
    public void validateTargetExists(Long targetId, TargetType targetType) {
        boolean exists = switch (targetType) {
            case FEED -> feedRepository.existsById(targetId);
            case COMMENT -> commentRepository.existsById(targetId);
        };
        if (!exists) {
            throw new IllegalArgumentException("존재하지 않는 신고 대상입니다.");
        }
    }

    @Override
    public void validateDuplicateReport(Long userId, Long targetId, TargetType targetType) {
        if (reportRepository.existsByReporterIdAndTargetIdAndTargetType(userId, targetId, targetType)) {
            throw new IllegalStateException("이미 신고한 대상입니다.");
        }
    }

    @Override
    public void validateSelfReport(Long userId, Long targetId, TargetType targetType) {
        Long authorId = switch (targetType) {
            case FEED -> feedRepository.findById(targetId)
                    .map(f -> f.getMember().getId())
                    .orElse(null);
            case COMMENT -> commentRepository.findById(targetId)
                    .map(c -> c.getMember().getId())
                    .orElse(null);
        };

        if (authorId != null && userId.equals(authorId)) {
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
