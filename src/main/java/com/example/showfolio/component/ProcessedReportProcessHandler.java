package com.example.showfolio.component;

import com.example.showfolio.entity.ProcessStatus;
import com.example.showfolio.entity.Report;
import com.example.showfolio.entity.ReportProcess;
import com.example.showfolio.port.ReportProcessHandler;
import com.example.showfolio.repository.ReportProcessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * PROCESS 상태의 신고 상태 처리를 위한 핸들러입니다.
 *
 */
@Component
@RequiredArgsConstructor
public class ProcessedReportProcessHandler implements ReportProcessHandler {

    private final ReportProcessRepository reportProcessRepository;

    @Override
    public ProcessStatus supportedStatus() {
        return ProcessStatus.PROCESSED;
    }

    @Override
    public void handle(Report report, Long adminId, String reason) {
        reportProcessRepository.save(ReportProcess.of(report, adminId, ProcessStatus.PROCESSED, reason));
    }
}
