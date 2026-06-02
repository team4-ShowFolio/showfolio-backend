-- AI 토큰 사용량 로그 테이블
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

