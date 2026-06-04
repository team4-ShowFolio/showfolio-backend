package com.example.showfolio.repository;

import com.example.showfolio.entity.Comment;

import java.util.List;

public interface CommentRepositoryCustom {

    // 특정 피드의 최상위 댓글 목록
    List<Comment> findCommentsByFeedId(Long feedId);

    // 특정 댓글의 대댓글 목록
    List<Comment> findRepliesByParentId(Long parentId);
}
