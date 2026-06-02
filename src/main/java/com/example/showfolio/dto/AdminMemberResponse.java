package com.example.showfolio.dto;

import com.example.showfolio.mock.Role;

import java.time.LocalDateTime;
import java.util.List;

public record AdminMemberResponse(
        Long id,
        String nickname,
        String username,
        String email,
        Role role,
        String status,
        LocalDateTime joinDate,
        List<String> techStack,
        long feedCount,
        int commentCount
) {
}
