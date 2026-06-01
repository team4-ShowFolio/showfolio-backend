package com.example.showfolio.project.dto;

// 검색 조건 (keyword: 제목/설명 LIKE, sort: latest|likes|views)
public record ProjectSearchCondition(
        String keyword,
        String sort
) {}
