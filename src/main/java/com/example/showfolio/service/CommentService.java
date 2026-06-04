package com.example.showfolio.service;

import com.example.showfolio.dto.request.CommentCreateRequest;
import com.example.showfolio.dto.request.CommentUpdateRequest;
import com.example.showfolio.dto.response.CommentResponse;
import com.example.showfolio.entity.Comment;
import com.example.showfolio.entity.Feed;
import com.example.showfolio.entity.Member;
import com.example.showfolio.repository.CommentRepository;
import com.example.showfolio.repository.FeedRepository;
import com.example.showfolio.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final FeedRepository feedRepository;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;

    // 댓글 작성
    @Transactional
    public CommentResponse createComment(Long feedId,
                                         CommentCreateRequest request,
                                         Long currentUserId) {

        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new EntityNotFoundException("피드를 찾을 수 없습니다"));

        Member member = memberRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다"));

        Comment parent = null;
        if (request.getParentId() != null) {
            parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("부모 댓글을 찾을 수 없습니다"));

            if (!parent.getFeed().getId().equals(feedId)) {
                throw new IllegalArgumentException("잘못된 부모 댓글입니다");
            }

            if (parent.isDeleted()) {
                throw new IllegalArgumentException("삭제된 댓글에는 답글을 달 수 없습니다");
            }
        }

        Comment comment = Comment.builder()
                .feed(feed)
                .member(member)
                .parent(parent)
                .content(request.getContent())
                .build();

        commentRepository.save(comment);

        // 댓글 알림 생성 (피드 작성자에게)
        notificationService.createCommentNotification(
                member,
                feed.getMember(),
                feed
        );

        return CommentResponse.from(comment, List.of());
    }

    // 댓글 목록 조회
    public List<CommentResponse> getComments(Long feedId) {

        feedRepository.findById(feedId)
                .orElseThrow(() -> new EntityNotFoundException("피드를 찾을 수 없습니다"));

        List<Comment> comments = commentRepository.findCommentsByFeedId(feedId);

        return comments.stream()
                .map(this::buildCommentResponse)
                .collect(Collectors.toList());
    }

    // 댓글 수정
    @Transactional
    public CommentResponse updateComment(Long commentId,
                                         CommentUpdateRequest request,
                                         Long currentUserId) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다"));

        if (comment.isDeleted()) {
            throw new IllegalArgumentException("삭제된 댓글은 수정할 수 없습니다");
        }

        if (!comment.getMember().getId().equals(currentUserId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다");
        }

        comment.update(request.getContent());

        List<CommentResponse> replies = commentRepository
                .findRepliesByParentId(commentId)
                .stream()
                .map(this::buildCommentResponse)
                .collect(Collectors.toList());

        return CommentResponse.from(comment, replies);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, Long currentUserId) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다"));

        if (comment.isDeleted()) {
            throw new IllegalArgumentException("이미 삭제된 댓글입니다");
        }

        if (!comment.getMember().getId().equals(currentUserId)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다");
        }

        comment.delete();
    }

    // 대댓글
    private CommentResponse buildCommentResponse(Comment comment) {

        List<CommentResponse> replies = commentRepository
                .findRepliesByParentId(comment.getId())
                .stream()
                .map(this::buildCommentResponse)
                .collect(Collectors.toList());

        return CommentResponse.from(comment, replies);
    }

    // 내가 쓴 댓글 목록
    public Page<CommentResponse> getMyComments(
            int page,
            int size,
            Long currentUserId) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> comments = commentRepository
                .findMyComments(currentUserId, pageable);

        return comments.map(comment ->
                CommentResponse.from(comment, List.of())
        );
    }
}
