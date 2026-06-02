package com.example.showfolio.project.repository;

import com.example.showfolio.project.dto.ProjectSearchCondition;
import com.example.showfolio.project.entity.Project;
import com.example.showfolio.project.entity.QProject;
import com.example.showfolio.project.entity.QProjectTech;
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

    public ProjectRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Project> search(ProjectSearchCondition condition, Pageable pageable) {
        QProject project = QProject.project;
        QProjectTech tech = QProjectTech.projectTech;

        List<Project> content = queryFactory
                .selectFrom(project)
                .distinct()
                .leftJoin(project.techStacks, tech)   // stacks 검색용 조인
                .where(
                        project.visibility.eq(Visibility.PUBLIC),
                        keywordContains(condition.keyword()),
                        authorIn(condition.authorIds()),
                        stacksIn(condition.stacks())
                )
                .orderBy(sortOrder(condition.sort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(project.countDistinct())
                .from(project)
                .leftJoin(project.techStacks, tech)
                .where(
                        project.visibility.eq(Visibility.PUBLIC),
                        keywordContains(condition.keyword()),
                        authorIn(condition.authorIds()),
                        stacksIn(condition.stacks())
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    // 제목/설명 부분일치 (대소문자 무시)
    private BooleanExpression keywordContains(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }
        QProject project = QProject.project;
        return project.title.containsIgnoreCase(keyword)
                .or(project.description.containsIgnoreCase(keyword));
    }

    // 작성자(memberId 목록) 필터
    private BooleanExpression authorIn(List<Long> authorIds) {
        return (authorIds == null || authorIds.isEmpty())
                ? null
                : QProject.project.memberId.in(authorIds);
    }

    // 기술스택 중 하나라도 포함 (OR)
    private BooleanExpression stacksIn(List<String> stacks) {
        if (stacks == null || stacks.isEmpty()) {
            return null;
        }
        return QProjectTech.projectTech.techName.in(stacks);
    }

    // 정렬: likes | (그 외=latest)
    private OrderSpecifier<?> sortOrder(String sort) {
        QProject project = QProject.project;
        if ("likes".equals(sort)) {
            return project.likeCount.desc();
        }
        return project.createdAt.desc();   // latest 기본
    }
}
