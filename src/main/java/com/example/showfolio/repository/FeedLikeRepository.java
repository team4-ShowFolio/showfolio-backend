package com.example.showfolio.repository;

import com.example.showfolio.entity.Feed;
import com.example.showfolio.entity.FeedLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedLikeRepository extends JpaRepository<FeedLike, Long> {

    // 좋아요 여부 확인
    boolean existsByFeedIdAndMemberId(Long feedId, Long memberId);

    // 좋아요 취소
    void deleteByFeedIdAndMemberId(Long feedId, Long memberId);

    // 좋아요 수
    int countByFeedId(Long feedId);

    // 좋아요한 피드 목록 (최신순)
    @Query("SELECT fl.feed FROM FeedLike fl " +
            "WHERE fl.member.id = :userId  " +
            "ORDER BY fl.createdAt DESC")
    Page<Feed> findLikedFeedsByMemberId(
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM FeedLike fl WHERE fl.feed.id = :feedId")
    void deleteByFeedId(@Param("feedId") Long feedId);
}
