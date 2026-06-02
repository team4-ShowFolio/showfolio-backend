package com.example.showfolio.repository;

import com.example.showfolio.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {
    // 내가 쓴 댓글 목록
    @Query("SELECT c FROM Comment c " +
            "WHERE c.user.id = :userId " +
            "AND c.deletedAt IS NULL " +
            "ORDER BY c.createdAt DESC")
    Page<Comment> findMyComments(
            @Param("userId") Long userId,
            Pageable pageable
    );
}
