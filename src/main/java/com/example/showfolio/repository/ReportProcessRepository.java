package com.example.showfolio.repository;

import com.example.showfolio.entity.ReportProcess;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportProcessRepository extends JpaRepository<ReportProcess, Long> {

    boolean existsByReportId(Long reportId);

    Optional<ReportProcess> findByReportId(Long reportId);
}
