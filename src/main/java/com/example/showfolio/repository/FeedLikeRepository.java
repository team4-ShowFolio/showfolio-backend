package com.example.showfolio.repository;

import com.example.showfolio.entity.FeedLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedLikeRepository extends JpaRepository<FeedLike, Long> {

    // 좋아요 여부 확인
    boolean existsByFeedIdAndUserId(Long feedId, Long userId);

    // 좋아요 취소
    void deleteByFeedIdAndUserId(Long feedId, Long userId);

    // 좋아요 수
    int countByFeedId(Long feedId);
}
