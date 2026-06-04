package com.example.showfolio.service;

import com.example.showfolio.dto.response.NotificationResponse;
import com.example.showfolio.entity.*;
import com.example.showfolio.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // 알림 목록 조회 (최신순)
    public Page<NotificationResponse> getNotifications(
            int page,
            int size,
            Long currentUserId) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notifications = notificationRepository
                .findByUserIdOrderByCreatedAtDesc(currentUserId, pageable);

        return notifications.map(NotificationResponse::from);
    }

    // 읽지 않은 알림 수 (벨 아이콘 숫자)
    public int getUnreadCount(Long currentUserId) {
        return notificationRepository
                .countByUserIdAndIsReadFalse(currentUserId);
    }

    // 단건 읽음 처리
    @Transactional
    public void readNotification(Long notificationId, Long currentUserId) {

        Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("알림을 찾을 수 없습니다"));

        if (!notification.getUser().getId().equals(currentUserId)) {
            throw new IllegalArgumentException("권한이 없습니다");
        }

        notification.read();
    }

    // 전체 읽음 처리
    @Transactional
    public void readAllNotifications(Long currentUserId) {
        notificationRepository.markAllAsRead(currentUserId);
    }

    // 좋아요 알림
    @Transactional
    public void createLikeNotification(User sender, User receiver, Feed feed) {

        if (sender.getId().equals(receiver.getId())) return;

        Notification notification = Notification.builder()
                .user(receiver)
                .sender(sender)
                .type(Notification.NotificationType.LIKE)
                .feed(feed)
                .build();

        notificationRepository.save(notification);
    }

    // 댓글 알림
    @Transactional
    public void createCommentNotification(User sender, User receiver, Feed feed) {

        if (sender.getId().equals(receiver.getId())) return;

        Notification notification = Notification.builder()
                .user(receiver)
                .sender(sender)
                .type(Notification.NotificationType.COMMENT)
                .feed(feed)
                .build();

        notificationRepository.save(notification);
    }

    // 멘션 알림
    @Transactional
    public void createMentionNotification(User sender, User receiver, Feed feed) {

        if (sender.getId().equals(receiver.getId())) return;

        Notification notification = Notification.builder()
                .user(receiver)
                .sender(sender)
                .type(Notification.NotificationType.MENTION)
                .feed(feed)
                .build();

        notificationRepository.save(notification);
    }
}
