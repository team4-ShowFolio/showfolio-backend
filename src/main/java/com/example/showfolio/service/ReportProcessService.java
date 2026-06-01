package com.example.showfolio.service;

import com.example.showfolio.dto.ReportProcessRequest;
import com.example.showfolio.entity.ProcessStatus;
import com.example.showfolio.entity.Report;
import com.example.showfolio.entity.ReportProcess;
import com.example.showfolio.port.ReportProcessHandler;
import com.example.showfolio.port.ReportProcessor;
import com.example.showfolio.repository.ReportProcessRepository;
import com.example.showfolio.repository.ReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ReportProcessService implements ReportProcessor {

    private final ReportRepository reportRepository;
    private final ReportProcessRepository reportProcessRepository;
    private final Map<ProcessStatus, ReportProcessHandler> handlers;

    public ReportProcessService(
            ReportRepository reportRepository,
            ReportProcessRepository reportProcessRepository,
            List<ReportProcessHandler> handlerList
    ) {
        this.reportRepository = reportRepository;
        this.reportProcessRepository = reportProcessRepository;
        this.handlers = handlerList.stream()
                .collect(Collectors.toMap(ReportProcessHandler::supportedStatus, Function.identity()));
    }

    @Override
    @Transactional
    public void process(ReportProcessRequest request) {
        if (reportProcessRepository.existsByReportId(request.reportId())) {
            throw new IllegalStateException("이미 처리된 신고입니다.");
        }

        Report report = reportRepository.findById(request.reportId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신고입니다."));

        ReportProcessHandler handler = handlers.get(request.status());
        if (handler == null) {
            throw new IllegalArgumentException("지원하지 않는 처리 상태입니다: " + request.status());
        }

        handler.handle(report, request.adminId(), request.reason());
    }

    @Override
    @Transactional
    public void update(ReportProcessRequest request) {
        ReportProcess reportProcess = reportProcessRepository.findByReportId(request.reportId())
                .orElseThrow(() -> new IllegalArgumentException("처리 내역이 존재하지 않습니다."));

        reportProcess.update(request.adminId(), request.status(), request.reason());
    }
}
