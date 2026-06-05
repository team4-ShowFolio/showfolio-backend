package com.example.showfolio.dto;

import java.time.Instant;

public record RecentFeedResponse(
        Long id,
        String title,
        Instant createdAt
) {
}
