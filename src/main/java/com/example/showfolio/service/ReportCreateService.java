package com.example.showfolio.service;

import com.example.showfolio.dto.ReportCreateRequest;
import com.example.showfolio.entity.Report;
import com.example.showfolio.event.ReportCreatedEvent;
import com.example.showfolio.port.ReportValidator;
import com.example.showfolio.port.Reporter;
import com.example.showfolio.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportCreateService implements Reporter {

    private final ReportRepository reportRepository;
    private final ReportValidator reportValidator;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void report(Long reporterId, ReportCreateRequest request) {
        reportValidator.validateDuplicateReport(reporterId, request.targetId(), request.targetType());
        reportValidator.validateSelfReport(reporterId, request.targetId(), request.targetType());
        reportValidator.validateTargetExists(request.targetId(), request.targetType());
        reportValidator.validateReportReason(request.reportReason(), request.content());

        Report report = reportRepository.save(Report.from(reporterId, request));
        eventPublisher.publishEvent(new ReportCreatedEvent(report));
    }
}
