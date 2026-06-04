package com.example.showfolio.repository;

import com.example.showfolio.entity.AiUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface AiUsageRepository extends JpaRepository<AiUsage, Long> {

    /**
     * 특정 회원의 특정 시점 이후 토큰 사용량 합계 (Input + Output)
     * 일별&월별 한도 검증, 사용량 통계 등에 활용
     *
     * - 일별 한도: since = 오늘 0시
     * - 월별 한도: since = 이번 달 1일 0시
     *
     * @param memberId 회원 ID
     * @param since    조회 시작 시점 (예: 오늘 0시)
     * @return 토큰 사용량 합계 (이력 없으면 0)
     */
    @Query("SELECT COALESCE(SUM(a.inputTokens + a.outputTokens), 0) " +
            "FROM AiUsage a " +
            "WHERE a.memberId = :memberId AND a.createdAt >= :since")
    int sumTokensSince(
            @Param("memberId") Long memberId,
            @Param("since") LocalDateTime since);
}
