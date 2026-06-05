package com.example.showfolio.event;

import com.example.showfolio.dto.ReportProcessRequest;
import com.example.showfolio.entity.ProcessStatus;
import com.example.showfolio.port.ReportProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ReportEventListener {

    private final ReportProcessor reportProcessor;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleReportCreated(ReportCreatedEvent event) {
        reportProcessor.process(event.report().getId(), new ReportProcessRequest(null, ProcessStatus.PENDING, null));
    }
}
