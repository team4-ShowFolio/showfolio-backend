package com.example.showfolio.controller;

import com.example.showfolio.dto.request.CommentCreateRequest;
import com.example.showfolio.dto.request.CommentUpdateRequest;
import com.example.showfolio.dto.response.CommentResponse;
import com.example.showfolio.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feeds/{feedId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 작성
    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long feedId,
            @Valid @RequestBody CommentCreateRequest request,

            // JWT 완성 후 @AuthenticationPrincipal Long currentUserId로 교체
            @RequestHeader("X-User-Id") Long currentUserId) {

        return ResponseEntity.ok(
                commentService.createComment(feedId, request, currentUserId));
    }

    // 댓글 목록 조회
    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(
            @PathVariable Long feedId) {

        return ResponseEntity.ok(commentService.getComments(feedId));
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long feedId,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentUpdateRequest request,

            // JWT 완성 후 @AuthenticationPrincipal Long currentUserId로 교체
            @RequestHeader("X-User-Id") Long currentUserId) {

        return ResponseEntity.ok(
                commentService.updateComment(commentId, request, currentUserId));
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long feedId,
            @PathVariable Long commentId,

            // JWT 완성 후 @AuthenticationPrincipal Long currentUserId로 교체
            @RequestHeader("X-User-Id") Long currentUserId) {

        commentService.deleteComment(commentId, currentUserId);
        return ResponseEntity.noContent().build();
    }
}
