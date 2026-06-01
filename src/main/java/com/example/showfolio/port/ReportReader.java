package com.example.showfolio.port;

import com.example.showfolio.dto.ReportSearchCondition;
import com.example.showfolio.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReportReader {

    Report getById(Long id);

    Page<Report> getAll(ReportSearchCondition condition, Pageable pageable);
}
