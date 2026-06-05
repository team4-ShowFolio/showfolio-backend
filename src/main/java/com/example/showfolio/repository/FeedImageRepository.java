package com.example.showfolio.repository;

import com.example.showfolio.entity.FeedImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeedImageRepository extends JpaRepository<FeedImage, Long> {

    // 특정 피드 이미지 목록
    List<FeedImage> findByFeedIdOrderByOrderNumAsc(Long feedId);

    // 특정 피드 이미지 전체 삭제
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM FeedImage fi WHERE fi.feed.id = :feedId")
    void deleteByFeedId(@Param("feedId") Long feedId);
}
