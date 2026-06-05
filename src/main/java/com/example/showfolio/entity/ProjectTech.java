package com.example.showfolio.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "project_tech",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_project_tech",
                columnNames = {"project_id", "tech_name"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ProjectTech {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "tech_name", nullable = false, length = 50)
    private String techName;

    // 같은 패키지의 Project에서만 호출 (양방향 연관 세팅)
    static ProjectTech of(Project project, String techName) {
        return ProjectTech.builder()
                .project(project)
                .techName(techName)
                .build();
    }
}
