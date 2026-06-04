package com.example.showfolio.repository;

import com.example.showfolio.entity.Feed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedRepository extends JpaRepository<Feed, Long>, FeedRepositoryCustom {

    long countByMemberId(Long memberId);

    List<Feed> findTop5ByMemberIdOrderByCreatedAtDesc(Long memberId);
}
