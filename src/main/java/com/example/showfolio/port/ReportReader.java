package com.example.showfolio.port;

import com.example.showfolio.dto.ReportProcessResponse;
import com.example.showfolio.dto.ReportProcessSearchCondition;
import com.example.showfolio.dto.ReportResponse;
import com.example.showfolio.dto.ReportSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ReportReader {

    Page<ReportResponse> getAll(ReportSearchCondition condition, Pageable pageable);

    Page<ReportProcessResponse> getAllProcesses(ReportProcessSearchCondition condition, Pageable pageable);

    Page<ReportResponse> getByTargetUserId(Long targetUserId, Pageable pageable);
}
