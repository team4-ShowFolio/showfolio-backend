package com.example.showfolio.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

// AI가 생성하는 포트폴리오 PDF용 정제 데이터.
// 타임리프 템플릿에 바인딩되는 구조.
@JsonIgnoreProperties(ignoreUnknown = true) // LLM에 의해 추가된 필드 무시
public record PortfolioPdfContentDto(

        @JsonProperty("refinedBio")
        String refinedBio,           // 정제된 자기소개

        @JsonProperty("careerSummary")
        String careerSummary,        // 한 줄 경력 요약

        @JsonProperty("projects")
        List<ProjectPdfItem> projects
) {
    public record ProjectPdfItem(
            @JsonProperty("title")
            String title,

            @JsonProperty("role")
            String role,             // 담당 역할 (정제)

            @JsonProperty("period")
            String period,           // "2026.03 ~ 2026.06" 형식

            @JsonProperty("description")
            String description,      // STAR 기법으로 다듬은 설명

            @JsonProperty("techStack")
            List<String> techStack
    ) {}
}
