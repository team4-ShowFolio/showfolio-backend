package com.example.showfolio.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "ai_usage",
        indexes = @Index(
                name = "idx_member_created",
                columnList = "memberId, createdAt"
        ))
public class AiUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;        // 사용자 id

    // AI 기능 종류
    // DESCRIPTION_IMPROVE, PORTFOLIO_FEEDBACK, RESUME_CONVERT
    @Column(nullable = false, length = 50)
    private String featureType;

    @Column(nullable = false)
    private int inputTokens;      // 토큰 사용량(입력)

    @Column(nullable = false)
    private int outputTokens;     // 토큰 사용량(출력)

    @Column(nullable = false)
    private LocalDateTime createdAt; // 토큰 사용 날짜

    public static AiUsage of(
            Long memberId, String featureType,
            int inputTokens, int outputTokens) {
        AiUsage usage = new AiUsage();
        usage.memberId = memberId;
        usage.featureType = featureType;
        usage.inputTokens = inputTokens;
        usage.outputTokens = outputTokens;
        usage.createdAt = LocalDateTime.now();
        return usage;
    }
}