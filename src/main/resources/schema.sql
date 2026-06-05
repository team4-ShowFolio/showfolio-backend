-- ==========================================
-- 0. 기존 데이터베이스 초기화 및 생성
-- ==========================================
DROP DATABASE IF EXISTS showfolio;
CREATE DATABASE showfolio DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE showfolio;

-- ==========================================
-- 1. member (회원 테이블)
-- ==========================================
CREATE TABLE `member` (
                          `id`                      BIGINT          NOT NULL AUTO_INCREMENT COMMENT '기본키',
                          `email`                   VARCHAR(100)    NOT NULL COMMENT 'UNIQUE',
                          `password`                VARCHAR(255)    NULL,
                          `nickname`                VARCHAR(50)     NOT NULL COMMENT 'UNIQUE',
                          `profile_image`           VARCHAR(255)    NULL,
                          `bio`                     VARCHAR(500)    NULL,
                          `role`                    ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
                          `subscription_type`       ENUM('FREE', 'PREMIUM') NOT NULL DEFAULT 'FREE',
                          `subscription_expired_at` DATETIME        NULL,
                          `created_at`              DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          `updated_at`              DATETIME        NULL,
                          `deleted_at`              DATETIME        NULL COMMENT 'Soft Delete',
                          PRIMARY KEY (`id`),
                          UNIQUE KEY `uk_member_email` (`email`),
                          UNIQUE KEY `uk_member_nickname` (`nickname`)
);

-- ==========================================
-- 2. social_account (소셜 계정 연동)
-- ==========================================
CREATE TABLE `social_account` (
                                  `id`            BIGINT      NOT NULL AUTO_INCREMENT,
                                  `provider`      ENUM('GOOGLE', 'KAKAO', 'GITHUB') NOT NULL,
                                  `provider_id`   VARCHAR(100) NOT NULL,
                                  `created_at`    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  `user_id`       BIGINT      NOT NULL,
                                  PRIMARY KEY (`id`),
                                  FOREIGN KEY (`user_id`) REFERENCES `member` (`id`) ON DELETE CASCADE
);

-- ==========================================
-- 3. refresh_token (보안 인증 토큰)
-- ==========================================
CREATE TABLE `refresh_token` (
                                 `id`            BIGINT      NOT NULL AUTO_INCREMENT,
                                 `token`         VARCHAR(255) NOT NULL,
                                 `expired_at`    DATETIME    NOT NULL,
                                 `created_at`    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 `user_id`       BIGINT      NOT NULL,
                                 PRIMARY KEY (`id`),
                                 FOREIGN KEY (`user_id`) REFERENCES `member` (`id`) ON DELETE CASCADE
);

-- ==========================================
-- 4. user_tech_stack (유저 보유 기술 스택)
-- ==========================================
CREATE TABLE `user_tech_stack` (
                                   `id`        BIGINT      NOT NULL AUTO_INCREMENT,
                                   `tech_name` VARCHAR(50) NOT NULL COMMENT 'React, Spring 등',
                                   `user_id`   BIGINT      NOT NULL,
                                   PRIMARY KEY (`id`),
                                   FOREIGN KEY (`user_id`) REFERENCES `member` (`id`) ON DELETE CASCADE
);

-- ==========================================
-- 5. follow (팔로우 관계 테이블)
-- ==========================================
CREATE TABLE `follow` (
                          `id`            BIGINT      NOT NULL AUTO_INCREMENT,
                          `follower_id`   BIGINT      NOT NULL COMMENT '팔로우 하는 사람',
                          `following_id`  BIGINT      NOT NULL COMMENT '팔로우 받는 사람',
                          `created_at`    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          PRIMARY KEY (`id`),
                          UNIQUE KEY `uk_follow` (`follower_id`, `following_id`),
                          FOREIGN KEY (`follower_id`) REFERENCES `member` (`id`) ON DELETE CASCADE,
                          FOREIGN KEY (`following_id`) REFERENCES `member` (`id`) ON DELETE CASCADE
);

-- ==========================================
-- 6. project (포트폴리오 대형 프로젝트)
-- ==========================================
CREATE TABLE `project` (
                           `id`                BIGINT          NOT NULL AUTO_INCREMENT COMMENT 'PK',
                           `user_id`           BIGINT          NOT NULL,
                           `title`             VARCHAR(100)    NOT NULL,
                           `description`       TEXT            NULL,
                           `github_url`        VARCHAR(500)    NULL,
                           `deploy_url`        VARCHAR(500)    NULL,
                           `start_date`        DATE            NULL COMMENT '프로젝트 시작일',
                           `end_date`          DATE            NULL COMMENT '프로젝트 종료일',
                           `is_team`           BOOLEAN         NOT NULL DEFAULT FALSE COMMENT '팀 프로젝트 여부',
                           `team_size`         INT             NULL DEFAULT 1,
                           `my_role`           VARCHAR(100)    NULL COMMENT '본인이 맡은 파트',
                           `visibility`        ENUM('PUBLIC', 'PRIVATE', 'FOLLOWERS_ONLY') NOT NULL DEFAULT 'PUBLIC',
                           `view_count`        INT             NOT NULL DEFAULT 0,
                           `like_count`        INT             NOT NULL DEFAULT 0,
                           `trouble_shooting`  TEXT            NULL,
                           `created_at`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           `updated_at`        DATETIME        NULL,
                           `deleted_at`        DATETIME        NULL COMMENT 'Soft Delete',
                           PRIMARY KEY (`id`),
                           FOREIGN KEY (`user_id`) REFERENCES `member` (`id`) ON DELETE CASCADE
);

-- ==========================================
-- 7. project_tech (프로젝트 사용 기술스택)
-- ==========================================
CREATE TABLE `project_tech` (
                                `id`            BIGINT      NOT NULL AUTO_INCREMENT COMMENT 'PK',
                                `tech_name`     VARCHAR(50) NOT NULL COMMENT 'React, Spring 등',
                                `project_id`    BIGINT      NOT NULL,
                                PRIMARY KEY (`id`),
                                UNIQUE KEY `uk_project_tech` (`project_id`, `tech_name`),
                                FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE CASCADE
);

-- ==========================================
-- 7-2. project_likes (✨ [누락 보구 완료] 유저님 피드 좋아요 규칙 동기화 개편)
-- ==========================================
CREATE TABLE `project_likes` (
                                 `id`            BIGINT      NOT NULL AUTO_INCREMENT,
                                 `project_id`    BIGINT      NOT NULL,
                                 `user_id`       BIGINT      NOT NULL,
                                 `created_at`    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `uk_project_member` (`project_id`, `user_id`),
                                 FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE CASCADE,
                                 FOREIGN KEY (`user_id`) REFERENCES `member` (`id`) ON DELETE CASCADE
);

-- ==========================================
-- 8. feeds (포트폴리오 피드 게시글)
-- ==========================================
CREATE TABLE `feeds` (
                         `id`            BIGINT      NOT NULL AUTO_INCREMENT,
                         `user_id`       BIGINT      NOT NULL COMMENT 'FK',
                         `title`         VARCHAR(100) NULL,
                         `content`       TEXT        NULL,
                         `visibility`    ENUM('PUBLIC', 'PRIVATE', 'FOLLOWERS_ONLY') NOT NULL DEFAULT 'PUBLIC',
                         `like_count`    INT         NOT NULL DEFAULT 0,
                         `project_id`    BIGINT      NULL COMMENT 'FK',
                         `created_at`    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         `updated_at`    DATETIME    NOT NULL,
                         `deleted_at`    DATETIME    NULL COMMENT '소프트 딜리트',
                         PRIMARY KEY (`id`),
                         FOREIGN KEY (`user_id`) REFERENCES `member` (`id`) ON DELETE CASCADE,
                         FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE SET NULL
);

-- ==========================================
-- 9. feed_images (피드 다중 이미지)
-- ==========================================
CREATE TABLE `feed_images` (
                               `id`        BIGINT      NOT NULL AUTO_INCREMENT,
                               `feed_id`   BIGINT      NOT NULL COMMENT 'FK',
                               `image_url` VARCHAR(500) NOT NULL,
                               `order_num` INT         NOT NULL DEFAULT 0,
                               PRIMARY KEY (`id`),
                               FOREIGN KEY (`feed_id`) REFERENCES `feeds` (`id`) ON DELETE CASCADE
);

-- ==========================================
-- 10. feed_tags (피드 검색용 해시태그)
-- ==========================================
CREATE TABLE `feed_tags` (
                             `id`        BIGINT      NOT NULL AUTO_INCREMENT,
                             `feed_id`   BIGINT      NOT NULL COMMENT 'FK',
                             `tag_name`  VARCHAR(50) NOT NULL,
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `uk_feed_tag` (`feed_id`, `tag_name`),
                             FOREIGN KEY (`feed_id`) REFERENCES `feeds` (`id`) ON DELETE CASCADE
);

-- ==========================================
-- 11. feed_likes (✨ [수정] 유저님 엔티티 소스코드에 맞춰 단일 PK 구조로 전격 보정)
-- ==========================================
CREATE TABLE `feed_likes` (
                              `id`            BIGINT      NOT NULL AUTO_INCREMENT,
                              `feed_id`       BIGINT      NOT NULL COMMENT 'FK',
                              `user_id`       BIGINT      NOT NULL COMMENT 'FK',
                              `created_at`    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `uk_feed_like` (`feed_id`, `user_id`),
                              FOREIGN KEY (`feed_id`) REFERENCES `feeds` (`id`) ON DELETE CASCADE,
                              FOREIGN KEY (`user_id`) REFERENCES `member` (`id`) ON DELETE CASCADE
);

-- ==========================================
-- 12. feed_mentions (✨ [수정] 유저님 엔티티 소스코드에 맞춰 단일 PK 구조로 전격 보정)
-- ==========================================
CREATE TABLE `feed_mentions` (
                                 `id`                BIGINT      NOT NULL AUTO_INCREMENT,
                                 `feed_id`           BIGINT      NOT NULL COMMENT 'FK',
                                 `mentioned_user_id` BIGINT      NOT NULL COMMENT 'FK',
                                 `created_at`        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `uk_feed_mention` (`feed_id`, `mentioned_user_id`),
                                 FOREIGN KEY (`feed_id`) REFERENCES `feeds` (`id`) ON DELETE CASCADE,
                                 FOREIGN KEY (`mentioned_user_id`) REFERENCES `member` (`id`) ON DELETE CASCADE
);

-- ==========================================
-- 13. comments (댓글 및 자식 대댓글 계층 구조)
-- ==========================================
CREATE TABLE `comments` (
                            `id`            BIGINT      NOT NULL AUTO_INCREMENT,
                            `user_id`       BIGINT      NOT NULL,
                            `feed_id`       BIGINT      NOT NULL COMMENT '피드 ID',
                            `parent_id`     BIGINT      NULL COMMENT '자기참조',
                            `content`       TEXT        NOT NULL,
                            `created_at`    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            `updated_at`    DATETIME    NOT NULL,
                            `deleted_at`    DATETIME    NULL COMMENT '소프트 딜리트',
                            PRIMARY KEY (`id`),
                            FOREIGN KEY (`user_id`) REFERENCES `member` (`id`) ON DELETE CASCADE,
                            FOREIGN KEY (`feed_id`) REFERENCES `feeds` (`id`) ON DELETE CASCADE,
                            FOREIGN KEY (`parent_id`) REFERENCES `comments` (`id`) ON DELETE CASCADE
);

-- ==========================================
-- 14. notifications (유저 실시간 알림 센터)
-- ==========================================
CREATE TABLE `notifications` (
                                 `id`            BIGINT      NOT NULL AUTO_INCREMENT,
                                 `user_id`       BIGINT      NOT NULL COMMENT 'FK',
                                 `sender_id`     BIGINT      NULL COMMENT 'FK',
                                 `type`          ENUM('LIKE','COMMENT','MENTION','FOLLOW') NOT NULL,
                                 `feed_id`       BIGINT      NULL COMMENT 'FK',
                                 `is_read`       BOOLEAN     NOT NULL DEFAULT FALSE,
                                 `created_at`    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 PRIMARY KEY (`id`),
                                 FOREIGN KEY (`user_id`) REFERENCES `member` (`id`) ON DELETE CASCADE,
                                 FOREIGN KEY (`sender_id`) REFERENCES `member` (`id`) ON DELETE SET NULL,
                                 FOREIGN KEY (`feed_id`) REFERENCES `feeds` (`id`) ON DELETE CASCADE
);

-- ==========================================
-- 15. report (유해 콘텐츠 신고 시스템)
-- ==========================================
CREATE TABLE `report` (
                          `id`             BIGINT          NOT NULL AUTO_INCREMENT,
                          `reporter_id`    BIGINT          NOT NULL,
                          `target_id`      BIGINT          NOT NULL COMMENT '피드 또는 대상 ID',
                          `target_user_id` BIGINT          NULL,
                          `target_type`    ENUM('FEED', 'COMMENT') NOT NULL,
                          `report_reason`  ENUM('SPAM', 'ADVERTISEMENT', 'ABUSIVE_LANGUAGE', 'HARASSMENT', 'FALSE_INFORMATION', 'FRAUD', 'VIOLENCE', 'ILLEGAL_CONTENT', 'COPYRIGHT_INFRINGEMENT', 'OFF_TOPIC', 'OTHER') NOT NULL,
                          `content`        VARCHAR(500)    NOT NULL,
                          `created_at`     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          PRIMARY KEY (`id`)
);

-- ==========================================
-- 16. report_proccess_history (✨ [수정] 소스코드 @JoinColumn(name="target_id") 매핑에 맞게 변수 정렬)
-- ==========================================
CREATE TABLE `report_proccess_history` (
                                           `id`            BIGINT      NOT NULL AUTO_INCREMENT,
                                           `target_id`     BIGINT      NOT NULL UNIQUE COMMENT '참조할 신고 ID (소스코드 바인딩 일치)',
                                           `admin_id`      BIGINT      NULL COMMENT '신고 처리 담당 관리자 ID',
                                           `status`        ENUM('PENDING', 'PROCESSED', 'REJECTED') NOT NULL,
                                           `reason`        VARCHAR(500) NULL,
                                           `created_at`    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                           `updated_at`    DATETIME    NULL,
                                           PRIMARY KEY (`id`),
                                           FOREIGN KEY (`target_id`) REFERENCES `report` (`id`) ON DELETE CASCADE
);

-- ==========================================
-- 17. ai_usage (AI API 사용량 트래킹 및 과금 방어)
-- ==========================================
CREATE TABLE ai_usage (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          member_id BIGINT NOT NULL,
                          feature_type VARCHAR(50) NOT NULL,
                          input_tokens INTEGER NOT NULL,
                          output_tokens INTEGER NOT NULL,
                          created_at DATETIME(6) NOT NULL,
                          PRIMARY KEY (id),
                          INDEX idx_member_created (member_id, created_at)
) ENGINE=InnoDB;

-- ==========================================
-- 📌 기초 데이터 INSERT
-- ==========================================
INSERT INTO member (email, password, nickname, role, created_at)
VALUES
    ('test1@test.com', '$2a$10$abcdefghijklmnopqrstuuVnlNsV8n4OJhw0oNHl.iMbqpMpsgU9C', '테스트유저1', 'USER', NOW()),
    ('test2@test.com', '$2a$10$abcdefghijklmnopqrstuuVnlNsV8n4OJhw0oNHl.iMbqpMpsgU9C', '테스트유저2', 'USER', NOW()),
    ('test3@test.com', '$2a$10$abcdefghijklmnopqrstuuVnlNsV8n4OJhw0oNHl.iMbqpMpsgU9C', '테스트유저3', 'USER', NOW());