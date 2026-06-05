package com.example.showfolio.repository;

import com.example.showfolio.entity.ReportProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ReportProcessRepository extends JpaRepository<ReportProcess, Long>, JpaSpecificationExecutor<ReportProcess> {

    boolean existsByReportId(Long reportId);

    Optional<ReportProcess> findByReportId(Long reportId);
}
