package com.example.showfolio.controller;

import com.example.showfolio.dto.response.NotificationResponse;
import com.example.showfolio.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // 알림 목록 조회
    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            // JWT 완성 후 @AuthenticationPrincipal Long currentUserId로 교체
            @RequestHeader("X-User-Id") Long currentUserId) {

        return ResponseEntity.ok(
                notificationService.getNotifications(page, size, currentUserId));
    }

    // 읽지 않은 알림 수
    @GetMapping("/unread-count")
    public ResponseEntity<Integer> getUnreadCount(
            // JWT 완성 후 @AuthenticationPrincipal Long currentUserId로 교체
            @RequestHeader("X-User-Id") Long currentUserId) {

        return ResponseEntity.ok(
                notificationService.getUnreadCount(currentUserId));
    }

    // 단건 읽음 처리
    @PatchMapping("/{notificationId}")
    public ResponseEntity<Void> readNotification(
            @PathVariable Long notificationId,
            // JWT 완성 후 @AuthenticationPrincipal Long currentUserId로 교체
            @RequestHeader("X-User-Id") Long currentUserId) {

        notificationService.readNotification(notificationId, currentUserId);
        return ResponseEntity.ok().build();
    }

    // 전체 읽음 처리
    @PatchMapping("/read-all")
    public ResponseEntity<Void> readAllNotifications(
            // JWT 완성 후 @AuthenticationPrincipal Long currentUserId로 교체
            @RequestHeader("X-User-Id") Long currentUserId) {

        notificationService.readAllNotifications(currentUserId);
        return ResponseEntity.ok().build();
    }
}
