package com.example.showfolio.project.dto;

import com.example.showfolio.project.entity.Project;
import com.example.showfolio.project.entity.Visibility;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ProjectResponse(
        Long id,
        Long memberId,
        String title,
        String description,
        String githubUrl,
        String deployUrl,
        LocalDate startDate,
        LocalDate endDate,
        boolean isTeam,
        Integer teamSize,
        String myRole,
        Visibility visibility,
        int viewCount,
        int likeCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ProjectResponse from(Project p) {
        return new ProjectResponse(
                p.getId(),
                p.getMemberId(),
                p.getTitle(),
                p.getDescription(),
                p.getGithubUrl(),
                p.getDeployUrl(),
                p.getStartDate(),
                p.getEndDate(),
                p.isTeam(),
                p.getTeamSize(),
                p.getMyRole(),
                p.getVisibility(),
                p.getViewCount(),
                p.getLikeCount(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
}
