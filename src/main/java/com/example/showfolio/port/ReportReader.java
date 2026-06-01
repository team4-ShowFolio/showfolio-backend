package com.example.showfolio.port;

import com.example.showfolio.dto.ReportProcessResponse;
import com.example.showfolio.dto.ReportResponse;
import com.example.showfolio.dto.ReportSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReportReader {

    ReportResponse getById(Long id);

    Page<ReportResponse> getAll(ReportSearchCondition condition, Pageable pageable);

    ReportProcessResponse getProcessByReportId(Long reportId);
}
