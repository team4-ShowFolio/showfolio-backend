package com.example.showfolio.dto.response;

import com.example.showfolio.entity.Notification;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponse {

    private Long id;
    private Long userId;
    private String userNickname;
    private Long senderId;
    private String senderNickname;
    private String senderProfileImage;
    private String type;
    private Long feedId;
    private Boolean isRead;
    private LocalDateTime createdAt;

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUser().getId())
                .userNickname(notification.getUser().getNickname())
                .senderId(notification.getSender().getId())
                .senderNickname(notification.getSender().getNickname())
                .senderProfileImage(notification.getSender().getProfileImage())
                .type(notification.getType().name())
                .feedId(notification.getFeed() != null
                        ? notification.getFeed().getId() : null)
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
