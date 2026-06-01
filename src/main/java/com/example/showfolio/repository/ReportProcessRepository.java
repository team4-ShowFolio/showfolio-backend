package com.example.showfolio.repository;

import com.example.showfolio.entity.ReportProcess;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportProcessRepository extends JpaRepository<ReportProcess, Long> {

    boolean existsByReportId(Long reportId);
}
