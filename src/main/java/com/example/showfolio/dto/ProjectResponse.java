package com.example.showfolio.dto;

import com.example.showfolio.entity.Project;
import com.example.showfolio.entity.ProjectTech;
import com.example.showfolio.enums.Visibility;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ProjectResponse(
        Long id,
        Long memberId,
        String title,
        String description,
        String troubleShooting,
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
        LocalDateTime updatedAt,
        List<String> stacks

) {
    public static ProjectResponse from(Project p) {
        return new ProjectResponse(
                p.getId(),
                p.getMemberId(),
                p.getTitle(),
                p.getDescription(),
                p.getTroubleShooting(),
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
                p.getUpdatedAt(),
                p.getTechStacks().stream().map(ProjectTech::getTechName).toList()
        );
    }
}
