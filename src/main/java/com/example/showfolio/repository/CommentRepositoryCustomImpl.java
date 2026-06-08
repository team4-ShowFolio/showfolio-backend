package com.example.showfolio.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.example.showfolio.entity.Comment;
import com.example.showfolio.entity.QComment;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QComment comment = QComment.comment;

    // 최상위 댓글 목록 (parent_id IS NULL + deleted_at IS NULL)
    @Override
    public List<Comment> findCommentsByFeedId(Long feedId) {

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(comment.feed.id.eq(feedId));
        builder.and(comment.parent.isNull());

        return queryFactory
                .selectFrom(comment)
                .leftJoin(comment.member).fetchJoin() //N+1 문제 해결
                .where(builder)
                .orderBy(comment.createdAt.asc())
                .fetch();
    }

    // 대댓글 목록 (deleted_at IS NULL)
    @Override
    public List<Comment> findRepliesByParentId(Long parentId) {

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(comment.parent.id.eq(parentId));

        return queryFactory
                .selectFrom(comment)
                .leftJoin(comment.member).fetchJoin() //N+1 문제 해결
                .where(builder)
                .orderBy(comment.createdAt.asc())
                .fetch();
    }
}
