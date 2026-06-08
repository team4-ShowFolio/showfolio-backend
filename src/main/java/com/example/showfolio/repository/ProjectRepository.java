package com.example.showfolio.repository;

import com.example.showfolio.entity.Member;
import com.example.showfolio.entity.Project;
import com.example.showfolio.enums.Visibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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

    // AI 제공용 — convertToResume (단건 + 기술스택 fetch join)
    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.techStacks WHERE p.id = :id")
    Optional<Project> findByIdWithTechStacks(@Param("id") Long id);

    // AI 제공용 — portfolioFeedback (회원 프로젝트 + 기술스택, 최신순)
    @Query("SELECT DISTINCT p FROM Project p " +
            "LEFT JOIN FETCH p.techStacks " +
            "WHERE p.memberId = :memberId " +
            "ORDER BY p.createdAt DESC")
    List<Project> findAllByMemberIdWithTechStacks(@Param("memberId") Long memberId, Pageable pageable);

}
