package com.example.showfolio.project.controller;

import com.example.showfolio.project.dto.ProjectCreateRequest;
import com.example.showfolio.project.dto.ProjectResponse;
import com.example.showfolio.project.dto.ProjectUpdateRequest;
import com.example.showfolio.project.service.ProjectService;
import com.example.showfolio.project.dto.ProjectSearchCondition;
import com.example.showfolio.project.dto.ProjectLikeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    /** 프로젝트 등록 */
    @PostMapping
    public ResponseEntity<ProjectResponse> create(
            Authentication authentication,
            @Valid @RequestBody ProjectCreateRequest request) {
        Long memberId = currentMemberId(authentication);
        ProjectResponse response = projectService.create(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** 단건 조회 (조회수 증가, PRIVATE는 본인만) */
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> get(
            Authentication authentication,
            @PathVariable Long projectId) {
        Long memberId = currentMemberId(authentication);
        return ResponseEntity.ok(projectService.get(projectId, memberId));
    }

    /** 수정 */
    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> update(
            Authentication authentication,
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectUpdateRequest request) {
        Long memberId = currentMemberId(authentication);
        return ResponseEntity.ok(projectService.update(projectId, memberId, request));
    }

    /** 삭제 (Soft Delete) */
    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> delete(
            Authentication authentication,
            @PathVariable Long projectId) {
        Long memberId = currentMemberId(authentication);
        projectService.delete(projectId, memberId);
        return ResponseEntity.noContent().build();
    }

    /** 본인 프로젝트 목록 (PRIVATE 포함) */
    @GetMapping("/me")
    public ResponseEntity<List<ProjectResponse>> getMyProjects(Authentication authentication) {
        Long memberId = currentMemberId(authentication);
        return ResponseEntity.ok(projectService.getMyProjects(memberId));
    }

    /** 특정 유저 프로젝트 목록 (PUBLIC만) */
    @GetMapping("/members/{memberId}")
    public ResponseEntity<List<ProjectResponse>> getMemberProjects(@PathVariable Long memberId) {
        return ResponseEntity.ok(projectService.getMemberProjects(memberId));
    }

    /** 검색 / 정렬 / 페이징 — GET /api/projects?keyword=&sort=&page=&size= */
    @GetMapping
    public ResponseEntity<Page<ProjectResponse>> getProjects(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "latest") String sort,
            @PageableDefault(size = 10) Pageable pageable) {
        ProjectSearchCondition condition = new ProjectSearchCondition(keyword, sort);
        return ResponseEntity.ok(projectService.search(condition, pageable));
    }

    // 인증 컨텍스트에서 memberId 추출 (JwtFilter가 username=memberId로 세팅 — feat/login 규약)
    private Long currentMemberId(Authentication authentication) {
        return Long.parseLong(authentication.getName());
    }
    
    /** 좋아요 토글 — POST /api/projects/{projectId}/likes */
    @PostMapping("/{projectId}/likes")
    public ResponseEntity<ProjectLikeResponse> toggleLike(
            Authentication authentication,
            @PathVariable Long projectId) {
        Long memberId = currentMemberId(authentication);
        return ResponseEntity.ok(projectService.toggleLike(projectId, memberId));
    }

}
