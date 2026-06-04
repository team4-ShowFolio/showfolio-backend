package com.example.showfolio.repository;

import com.example.showfolio.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    // 내 알림 목록 (최신순)
    Page<Notification> findByUserIdOrderByCreatedAtDesc(
            Long userId,
            Pageable pageable
    );

    // 읽지 않은 알림 수 (벨 아이콘 숫자 표시용)
    int countByUserIdAndIsReadFalse(Long userId);

    // 전체 읽음 처리
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true " +
            "WHERE n.user.id = :userId AND n.isRead = false")
    void markAllAsRead(@Param("userId") Long userId);
}
