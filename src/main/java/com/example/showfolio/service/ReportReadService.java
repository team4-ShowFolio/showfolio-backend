package com.example.showfolio.service;

import com.example.showfolio.dto.ReportResponse;
import com.example.showfolio.dto.ReportSearchCondition;
import com.example.showfolio.entity.Report;
import com.example.showfolio.port.ReportReader;
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

    @Override
    @Transactional(readOnly = true)
    public ReportResponse getById(Long id) {
        return reportRepository.findById(id)
                .map(ReportResponse::from)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신고입니다."));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReportResponse> getAll(ReportSearchCondition condition, Pageable pageable) {
        Specification<Report> spec = (root, query, cb) -> null;

        if (condition.targetType() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("targetType"), condition.targetType()));
        }
        if (condition.reporterId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("reporterId"), condition.reporterId()));
        }

        return reportRepository.findAll(spec, pageable).map(ReportResponse::from);
    }
}
