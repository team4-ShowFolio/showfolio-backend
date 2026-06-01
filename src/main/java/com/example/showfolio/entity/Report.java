package com.example.showfolio.entity;

import com.example.showfolio.dto.CreateReportRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Getter
@Table(name = "report")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO 회원 테이블과 피드, 댓글 관련한 기능 병합되면 연관관계 설정할지 판단 필요
    @Column(nullable = false)
    private Long reporterId;

    @Column(nullable = false)
    private Long targetId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TargetType targetType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportReason reportReason;

    @Column(nullable = false, length = 500)
    private String content;

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    public static Report from(CreateReportRequest request) {
        Report report = new Report();
        report.reporterId = request.reporterId();
        report.targetId = request.targetId();
        report.targetType = request.targetType();
        report.reportReason = request.reportReason();
        report.content = (request.content() == null || request.content().isBlank()) ? "내용없음" : request.content();

        return report;
    }
}
