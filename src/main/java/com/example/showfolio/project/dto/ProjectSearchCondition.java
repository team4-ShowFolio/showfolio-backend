package com.example.showfolio.project.dto;

import java.util.List;

// 검색 조건 (keyword: 제목/설명 LIKE, sort: latest|likes|views)
public record ProjectSearchCondition(
        String keyword,
        List<String> stacks,
        List<Long> authorIds,    // memberId
        String sort     // latest | likes
) {}
