package com.example.showfolio.repository;

import com.example.showfolio.entity.Feed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FeedRepositoryCustom {

    // 전체 피드 목록 (공개범위 필터링)
    Page<Feed> findPublicFeeds(Pageable pageable);

    // 팔로잉 피드 목록 (PUBLIC, FOLLOWERS_ONLY)
    Page<Feed> findFollowingFeeds(List<Long> followingIds, Pageable pageable);

    // 내 피드 목록 (전체 공개범위)
    Page<Feed> findMyFeeds(Long userId, Pageable pageable);
}
