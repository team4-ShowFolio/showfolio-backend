package com.example.showfolio.repository;

import com.example.showfolio.entity.Feed;
import com.example.showfolio.entity.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeedRepository extends JpaRepository<Feed, Long>, FeedRepositoryCustom {

    long countByMemberId(Long memberId);

    List<Feed> findTop5ByMemberIdOrderByCreatedAtDesc(Long memberId);

    // 포트폴리오 피드백용, N개 조회
    List<Feed> findByMember(Member member, Pageable pageable);

}
