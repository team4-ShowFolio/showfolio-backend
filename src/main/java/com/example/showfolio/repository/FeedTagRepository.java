package com.example.showfolio.repository;

import com.example.showfolio.entity.FeedTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeedTagRepository extends JpaRepository<FeedTag, Long> {

    // 특정 피드 태그 목록
    List<FeedTag> findByFeedId(Long feedId);

    // 특정 피드 태그 전체 삭제
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM FeedTag ft WHERE ft.feed.id = :feedId")
    void deleteByFeedId(@Param("feedId") Long feedId);
}
