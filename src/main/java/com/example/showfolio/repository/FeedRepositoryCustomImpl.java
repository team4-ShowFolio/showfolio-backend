package com.example.showfolio.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.example.showfolio.entity.Feed;
import com.example.showfolio.entity.QFeed;
import com.example.showfolio.entity.Visibility;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;


import java.util.List;

@RequiredArgsConstructor
public class FeedRepositoryCustomImpl implements FeedRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QFeed feed = QFeed.feed;

    // 전체 피드 목록 (PUBLIC)
    @Override
    public Page<Feed> findPublicFeeds(Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(feed.visibility.eq(Visibility.PUBLIC));

        List<Feed> content = queryFactory
                .selectFrom(feed)
                .where(builder)
                .orderBy(feed.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(feed.count())
                .from(feed)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    // 팔로잉 피드 목록 (PUBLIC, FOLLOWERS_ONLY)
    @Override
    public Page<Feed> findFollowingFeeds(List<Long> followingIds, Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(feed.user.id.in(followingIds));
        builder.and(feed.visibility.in(
                Visibility.PUBLIC,
                Visibility.FOLLOWERS_ONLY
        ));

        List<Feed> content = queryFactory
                .selectFrom(feed)
                .where(builder)
                .orderBy(feed.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(feed.count())
                .from(feed)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    // 내 피드 목록 (전체 공개)
    @Override
    public Page<Feed> findMyFeeds(Long userId, Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(feed.user.id.eq(userId));

        List<Feed> content = queryFactory
                .selectFrom(feed)
                .where(builder)
                .orderBy(feed.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(feed.count())
                .from(feed)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }
}
