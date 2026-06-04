package com.example.showfolio.controller;

import com.example.showfolio.dto.response.CommentResponse;
import com.example.showfolio.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class MyCommentController {

    private final CommentService commentService;

    // 내가 쓴 댓글 목록(댓글 클릭 시 feedId로 해당 피드로 이동)
    @GetMapping("/my")
    public ResponseEntity<Page<CommentResponse>> getMyComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long currentUserId = Long.parseLong(userDetails.getUsername());

        return ResponseEntity.ok(
                commentService.getMyComments(page, size, currentUserId));
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long currentUserId = Long.parseLong(userDetails.getUsername());

        commentService.deleteComment(commentId, currentUserId);
        return ResponseEntity.noContent().build();
    }
}
