package com.example.showfolio.project.repository;

import com.example.showfolio.project.entity.Project;
import com.example.showfolio.project.entity.Visibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long>, ProjectRepositoryCustom {

    // 본인 프로젝트 목록 (PRIVATE 포함) — getMyProjects
    Page<Project> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);

    // 특정 유저의 PUBLIC 프로젝트만 — getMemberProjects
    List<Project> findByMemberIdAndVisibilityOrderByCreatedAtDesc(Long memberId, Visibility visibility);

    // AI 제공용 — portfolioFeedback (전체 프로젝트)
    List<Project> findAllByMemberId(Long memberId);

    // 닉네임 → memberId (member 테이블 직접 조회)
    @Query(value = "SELECT id FROM member WHERE nickname = :nickname", nativeQuery = true)
    List<Long> findMemberIdsByNickname(@Param("nickname") String nickname);

}
