package com.example.showfolio.repository;

import com.example.showfolio.entity.FeedMention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeedMentionRepository extends JpaRepository<FeedMention, Long> {

    // 특정 피드 멘션 목록
    List<FeedMention> findByFeedId(Long feedId);

    // 특정 피드 멘션 전체 삭제
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM FeedMention fm WHERE fm.feed.id = :feedId")
    void deleteByFeedId(@Param("feedId") Long feedId);
}
