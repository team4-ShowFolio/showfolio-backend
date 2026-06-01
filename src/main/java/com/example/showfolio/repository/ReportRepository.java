package com.example.showfolio.repository;

import com.example.showfolio.entity.Report;
import com.example.showfolio.entity.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    boolean existsByReporterIdAndTargetIdAndTargetType(Long reporterId, Long targetId, TargetType targetType);
}
