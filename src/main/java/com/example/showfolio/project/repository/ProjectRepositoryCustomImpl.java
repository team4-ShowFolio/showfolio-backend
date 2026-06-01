package com.example.showfolio.project.repository;

import com.example.showfolio.project.dto.ProjectSearchCondition;
import com.example.showfolio.project.entity.Project;
import com.example.showfolio.project.entity.QProject;
import com.example.showfolio.project.entity.Visibility;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.util.List;

public class ProjectRepositoryCustomImpl implements ProjectRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // 전역 Bean 없이 EntityManager로 직접 생성(팀 설정 충돌 고려)
    public ProjectRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Project> search(ProjectSearchCondition condition, Pageable pageable) {
        QProject project = QProject.project;

        // 컨텐츠 조회
        List<Project> content = queryFactory
                .selectFrom(project)
                .where(
                        project.visibility.eq(Visibility.PUBLIC),  // 공개 목록은 PUBLIC만
                        keywordContains(condition.keyword())        // null이면 자동 무시
                )
                .orderBy(sortOrder(condition.sort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 카운트 조회 (페이징 total)
        Long total = queryFactory
                .select(project.count())
                .from(project)
                .where(
                        project.visibility.eq(Visibility.PUBLIC),
                        keywordContains(condition.keyword())
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    // 제목 또는 설명에 keyword 포함 (대소문자 무시). 빈 값이면 null → where에서 무시
    private BooleanExpression keywordContains(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }
        QProject project = QProject.project;
        return project.title.containsIgnoreCase(keyword)
                .or(project.description.containsIgnoreCase(keyword));
    }

    // 정렬: likes | views | (그 외=latest)
    private OrderSpecifier<?> sortOrder(String sort) {
        QProject project = QProject.project;
        if (sort == null) {
            return project.createdAt.desc();
        }
        return switch (sort) {
            case "likes" -> project.likeCount.desc();
            case "views" -> project.viewCount.desc();
            default -> project.createdAt.desc();
        };
    }
}
