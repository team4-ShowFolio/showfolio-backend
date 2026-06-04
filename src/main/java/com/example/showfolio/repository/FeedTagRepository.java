package com.example.showfolio.repository;

import com.example.showfolio.entity.FeedTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedTagRepository extends JpaRepository<FeedTag, Long> {

    // 특정 피드 태그 목록
    List<FeedTag> findByFeedId(Long feedId);

    // 특정 피드 태그 전체 삭제
    void deleteByFeedId(Long feedId);
}
