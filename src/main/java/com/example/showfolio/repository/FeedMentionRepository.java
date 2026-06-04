package com.example.showfolio.repository;

import com.example.showfolio.entity.FeedMention;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedMentionRepository extends JpaRepository<FeedMention, Long> {

    // 특정 피드 멘션 목록
    List<FeedMention> findByFeedId(Long feedId);

    // 특정 피드 멘션 전체 삭제
    void deleteByFeedId(Long feedId);
}
