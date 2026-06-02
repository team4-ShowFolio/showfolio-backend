package com.example.showfolio.dto;

import java.time.Instant;
import java.util.List;

public record AdminMemberResponse(
        Long id,
        String nickname,
        String username,
        String email,
        String role,
        String status,
        Instant joinDate,
        List<String> techStack,
        int feedCount,
        int commentCount
) {
}
