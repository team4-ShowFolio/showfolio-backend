package com.example.showfolio.repository;

import com.example.showfolio.entity.Report;
import com.example.showfolio.entity.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ReportRepository extends JpaRepository<Report, Long>, JpaSpecificationExecutor<Report> {
    boolean existsByReporterIdAndTargetIdAndTargetType(Long reporterId, Long targetId, TargetType targetType);

    long countByTargetUserId(Long targetUserId);
}
