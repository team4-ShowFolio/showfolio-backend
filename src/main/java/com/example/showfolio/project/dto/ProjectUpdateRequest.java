package com.example.showfolio.project.dto;

import com.example.showfolio.project.entity.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ProjectUpdateRequest(
        @NotBlank(message = "제목은 필수입니다")
        @Size(max = 100) String title,

        String description,
        @Size(max = 500) String githubUrl,
        @Size(max = 500) String deployUrl,
        LocalDate startDate,
        LocalDate endDate,
        boolean isTeam,
        Integer teamSize,
        @Size(max = 100) String myRole,
        Visibility visibility
) {}
