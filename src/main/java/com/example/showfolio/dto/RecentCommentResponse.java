package com.example.showfolio.dto;

import java.time.Instant;

public record RecentCommentResponse(
        Long id,
        String content,
        Instant createdAt
) {
}
