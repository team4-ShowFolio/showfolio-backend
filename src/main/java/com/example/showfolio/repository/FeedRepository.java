package com.example.showfolio.repository;

import com.example.showfolio.entity.Feed;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeedRepository extends JpaRepository<Feed, Long>, FeedRepositoryCustom {

    // 포트폴리오 피드백용, 최근 N개 조회
    @Query("SELECT f FROM Feed f WHERE f.member.id = :memberId " +
            "ORDER BY f.createdAt DESC")
    List<Feed> findRecentByMemberId(@Param("memberId") Long memberId, Pageable pageable);
}
