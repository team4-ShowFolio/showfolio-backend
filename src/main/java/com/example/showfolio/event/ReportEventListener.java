package com.example.showfolio.event;

import com.example.showfolio.entity.ProcessStatus;
import com.example.showfolio.entity.ReportProcess;
import com.example.showfolio.repository.ReportProcessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ReportEventListener {

    private final ReportProcessRepository reportProcessRepository;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleReportCreated(ReportCreatedEvent event) {
        reportProcessRepository.save(ReportProcess.of(event.report(), null, ProcessStatus.PENDING, null));
    }
}
