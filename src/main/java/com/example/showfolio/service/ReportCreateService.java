package com.example.showfolio.service;

import com.example.showfolio.dto.CreateReportRequest;
import com.example.showfolio.entity.Report;
import com.example.showfolio.port.ReportValidator;
import com.example.showfolio.port.Reporter;
import com.example.showfolio.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportCreateService implements Reporter {

    private final ReportRepository reportRepository;
    private final ReportValidator reportValidator;

    @Override
    @Transactional
    public void report(CreateReportRequest request) {
        // 다른 서비스에서 호출할 가능성이 있으므로, 검증 로직도 작성합니다.
        reportValidator.validateDuplicateReport(request.reporterId(), request.targetId(), request.targetType());
        reportValidator.validateSelfReport(request.reporterId(), request.targetId(), request.targetType());
        reportValidator.validateTargetExists(request.targetId(), request.targetType());
        reportValidator.validateReportReason(request.reportReason(), request.content());

        // 신고 관련 DB 작업을 진행합니다.
        Report report = Report.from(request);
        reportRepository.save(report);
    }
}
