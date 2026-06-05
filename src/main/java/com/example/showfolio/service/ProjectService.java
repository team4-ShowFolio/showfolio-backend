package com.example.showfolio.service;

import com.example.showfolio.dto.ProjectCreateRequest;
import com.example.showfolio.dto.ProjectResponse;
import com.example.showfolio.dto.ProjectUpdateRequest;
import com.example.showfolio.entity.Project;
import com.example.showfolio.enums.Visibility;
import com.example.showfolio.exception.ProjectAccessDeniedException;
import com.example.showfolio.exception.ProjectNotFoundException;
import com.example.showfolio.repository.ProjectRepository;
import com.example.showfolio.dto.ProjectSearchCondition;
import com.example.showfolio.dto.ProjectLikeResponse;
import com.example.showfolio.entity.ProjectLike;
import com.example.showfolio.repository.ProjectLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;




import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)   // 기본은 읽기 전용, 쓰기 메서드만 @Transactional 재선언
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectLikeRepository projectLikeRepository;


    /** 프로젝트 등록 */
    @Transactional
    public ProjectResponse create(Long memberId, ProjectCreateRequest req) {
        Project project = Project.builder()
                .memberId(memberId)
                .title(req.title())
                .description(req.description())
                .troubleShooting(req.troubleShooting())
                .githubUrl(req.githubUrl())
                .deployUrl(req.deployUrl())
                .startDate(req.startDate())
                .endDate(req.endDate())
                .isTeam(req.isTeam())
                .teamSize(req.teamSize() != null ? req.teamSize() : 1)
                .myRole(req.myRole())
                .visibility(req.visibility() != null ? req.visibility() : Visibility.PUBLIC)
                .build();
        
        if (req.stacks() != null) {
            req.stacks().forEach(project::addTechStack);
        }
        return ProjectResponse.from(projectRepository.save(project));
    }

    /** 단건 조회 (+조회수 증가, PRIVATE는 본인만) */
    @Transactional
    public ProjectResponse get(Long projectId, Long currentMemberId) {
        Project project = findActive(projectId);
        if (project.isPrivate() && !project.isOwnedBy(currentMemberId)) {
            throw new ProjectAccessDeniedException();
        }
        project.increaseViewCount();   // Dirty Checking으로 반영
        return ProjectResponse.from(project);
    }

    /** 수정 (본인 소유 검증) */
    @Transactional
    public ProjectResponse update(Long projectId, Long memberId, ProjectUpdateRequest req) {
        Project project = findActive(projectId);
        validateOwner(project, memberId);
        project.update(req.title(), req.description(), req.githubUrl(), req.deployUrl(),
                req.startDate(), req.endDate(), req.isTeam(), req.teamSize(),
                req.myRole(), req.visibility(), req.troubleShooting());
        project.updateTechStacks(req.stacks());

        return ProjectResponse.from(project);
    }

    /** 삭제 (Soft Delete, 본인 소유 검증) */
    @Transactional
    public void delete(Long projectId, Long memberId) {
        Project project = findActive(projectId);
        validateOwner(project, memberId);
        project.softDelete();
    }

    /** 본인 프로젝트 목록 (PRIVATE 포함) — 페이징 */
    public Page<ProjectResponse> getMyProjects(Long memberId, Pageable pageable) {
        return projectRepository.findByMemberIdOrderByCreatedAtDesc(memberId, pageable)
                .map(ProjectResponse::from);
    }


    /** 특정 유저 프로젝트 목록 (PUBLIC만) */
    public List<ProjectResponse> getMemberProjects(Long memberId) {
        return projectRepository.findByMemberIdAndVisibilityOrderByCreatedAtDesc(memberId, Visibility.PUBLIC)
                .stream().map(ProjectResponse::from).toList();
    }

    /** 검색 / 정렬 / 페이징 (PUBLIC만) */
    public Page<ProjectResponse> search(String keyword, List<String> stacks,
                                        String authorNickname, String sort, Pageable pageable) {
        List<Long> authorIds = null;
        if (StringUtils.hasText(authorNickname)) {
            authorIds = projectRepository.findMemberIdsByNickname(authorNickname);
            if (authorIds.isEmpty()) {
                return Page.empty(pageable);   // 해당 닉네임 작성자 없음 → 빈 결과
            }
        }
        ProjectSearchCondition condition = new ProjectSearchCondition(keyword, stacks, authorIds, sort);
        return projectRepository.search(condition, pageable)
                .map(ProjectResponse::from);
    }


    /** 좋아요 토글 (등록/취소) */
    @Transactional
    public ProjectLikeResponse toggleLike(Long projectId, Long memberId) {
        Project project = findActive(projectId);

        // PRIVATE 프로젝트는 본인만 좋아요 가능 (조회 권한과 동일)
        if (project.isPrivate() && !project.isOwnedBy(memberId)) {
            throw new ProjectAccessDeniedException();
        }

        return projectLikeRepository.findByProjectIdAndMemberId(projectId, memberId)
                .map(like -> {                       // 이미 좋아요 → 취소
                    projectLikeRepository.delete(like);
                    project.decreaseLikeCount();
                    return new ProjectLikeResponse(false, project.getLikeCount());
                })
                .orElseGet(() -> {                   // 좋아요 없음 → 등록
                    projectLikeRepository.save(ProjectLike.builder()
                            .projectId(projectId)
                            .memberId(memberId)
                            .build());
                    project.increaseLikeCount();
                    return new ProjectLikeResponse(true, project.getLikeCount());
                });
    }

    // === private helpers ===

    private Project findActive(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(ProjectNotFoundException::new);   // @SQLRestriction으로 삭제된 건 자동 제외
    }

    private void validateOwner(Project project, Long memberId) {
        if (!project.isOwnedBy(memberId)) {
            throw new ProjectAccessDeniedException();
        }
    }
}
