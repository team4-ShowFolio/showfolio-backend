package com.example.showfolio.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Getter
@Table(name = "report_proccess_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ReportProcess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", nullable = false, unique = true)
    private Report report;

    @Column
    private Long adminId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProcessStatus status;

    @Column(length = 500)
    private String reason;

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    @LastModifiedBy
    @Column(updatable = true)
    private Instant updatedAt;

    public static ReportProcess of(Report report, Long adminId, ProcessStatus status, String reason) {
        ReportProcess reportProcess = new ReportProcess();
        reportProcess.report = report;
        reportProcess.adminId = adminId;
        reportProcess.status = status;
        reportProcess.reason = reason;
        return reportProcess;
    }

    public void update(Long adminId, ProcessStatus status, String reason) {
        this.adminId = adminId;
        this.status = status;
        this.reason = reason;
    }
}
