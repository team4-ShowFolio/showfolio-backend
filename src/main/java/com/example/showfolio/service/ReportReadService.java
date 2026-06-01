package com.example.showfolio.service;

import com.example.showfolio.dto.ReportProcessResponse;
import com.example.showfolio.dto.ReportProcessSearchCondition;
import com.example.showfolio.dto.ReportResponse;
import com.example.showfolio.dto.ReportSearchCondition;
import com.example.showfolio.entity.Report;
import com.example.showfolio.entity.ReportProcess;
import com.example.showfolio.port.ReportReader;
import com.example.showfolio.repository.ReportProcessRepository;
import com.example.showfolio.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportReadService implements ReportReader {

    private final ReportRepository reportRepository;
    private final ReportProcessRepository reportProcessRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ReportResponse> getAll(ReportSearchCondition condition, Pageable pageable) {
        Specification<Report> spec = (root, query, cb) -> null;

        if (condition.targetId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("targetId"), condition.targetId()));
        }
        if (condition.reporterId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("reporterId"), condition.reporterId()));
        }
        if (condition.targetType() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("targetType"), condition.targetType()));
        }
        if (condition.reportReason() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("reportReason"), condition.reportReason()));
        }

        return reportRepository.findAll(spec, pageable).map(ReportResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReportProcessResponse> getAllProcesses(ReportProcessSearchCondition condition, Pageable pageable) {
        Specification<ReportProcess> spec = (root, query, cb) -> null;

        if (condition.reportId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("report").get("id"), condition.reportId()));
        }
        if (condition.reporterId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("report").get("reporterId"), condition.reporterId()));
        }
        if (condition.status() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), condition.status()));
        }

        return reportProcessRepository.findAll(spec, pageable).map(ReportProcessResponse::from);
    }
}
