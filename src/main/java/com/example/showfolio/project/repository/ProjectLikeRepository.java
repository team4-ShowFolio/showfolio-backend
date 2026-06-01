package com.example.showfolio.project.repository;

import com.example.showfolio.project.entity.ProjectLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectLikeRepository extends JpaRepository<ProjectLike, Long> {
    Optional<ProjectLike> findByProjectIdAndMemberId(Long projectId, Long memberId);
}
