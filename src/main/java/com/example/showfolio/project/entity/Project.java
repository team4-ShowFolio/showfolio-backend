package com.example.showfolio.project.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "project")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@SQLRestriction("deleted_at IS NULL")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long memberId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "trouble_shooting", columnDefinition = "TEXT")
    private String troubleShooting;

    @Column(name = "github_url", length = 500)
    private String githubUrl;

    @Column(name = "deploy_url", length = 500)
    private String deployUrl;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_team", nullable = false)
    @Builder.Default
    private boolean isTeam = false;

    @Column(name = "team_size")
    @Builder.Default
    private Integer teamSize = 1;

    @Column(name = "my_role", length = 100)
    private String myRole;

    @Enumerated(EnumType.STRING)   // ENUM은 STRING 매핑 필수
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Visibility visibility = Visibility.PUBLIC;

    @Column(name = "view_count", nullable = false)
    @Builder.Default
    private int viewCount = 0;

    @Column(name = "like_count", nullable = false)
    @Builder.Default
    private int likeCount = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;   // 소프트딜리트 시점 기록

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProjectTech> techStacks = new ArrayList<>();


    // === 비즈니스 메서드 ===

    /** 본인 소유 여부 */
    public boolean isOwnedBy(Long memberId) {
        return this.memberId.equals(memberId);
    }

    public boolean isPrivate() {
        return this.visibility == Visibility.PRIVATE;
    }

    /** 프로젝트 정보 수정 (Dirty Checking) */
    public void update(String title, String description, String githubUrl, String deployUrl,
                       LocalDate startDate, LocalDate endDate, boolean isTeam,
                       Integer teamSize, String myRole, Visibility visibility, String troubleShooting) {
        this.title = title;
        this.description = description;
        this.troubleShooting = troubleShooting;
        this.githubUrl = githubUrl;
        this.deployUrl = deployUrl;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isTeam = isTeam;
        this.teamSize = teamSize;
        this.myRole = myRole;
        if (visibility != null) {
            this.visibility = visibility;
        }
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    /** 소프트 딜리트 */
    public void softDelete() {
        this.deletedAt = java.time.LocalDateTime.now();
    }

    public void addTechStack(String techName) {
        this.techStacks.add(ProjectTech.of(this, techName));
    }

    /** 기술스택 전체 교체 (수정 시) — orphanRemoval로 기존 행 삭제 후 재등록 */
    public void updateTechStacks(List<String> techNames) {
        this.techStacks.clear();
        if (techNames != null) {
            techNames.forEach(this::addTechStack);
        }
    }

    
}