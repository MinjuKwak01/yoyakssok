-- 알림 발송 이력 테이블 생성
-- 푸시 알림 중복 발송 방지 및 이력 관리
CREATE TABLE IF NOT EXISTS notification_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    is_sent BOOLEAN NOT NULL DEFAULT FALSE,
    sent_at DATETIME(6),
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    
    CONSTRAINT fk_notification_history_member 
        FOREIGN KEY (member_id) 
        REFERENCES member_tb(id) 
        ON DELETE CASCADE
);

-- 마이그레이션 로그
-- 변경 사항: 푸시 알림 발송 이력 관리를 위한 notification_history 테이블 생성
-- 목적: 알림 중복 발송 방지 및 발송 이력 추적
-- 필드 설명:
--   - id: 기본키 (AUTO_INCREMENT)
--   - member_id: 회원 참조 (NOT NULL, CASCADE DELETE)
--   - notification_type: 알림 유형 (DOCUMENT_REMINDER, USAGE_MILESTONE 등)
--   - title: 알림 제목
--   - content: 알림 내용
--   - is_sent: 발송 완료 여부 (기본값: FALSE)
--   - sent_at: 실제 발송 시간
--   - created_at/updated_at: 생성/수정 시간
-- 제약 조건:
--   - 회원 삭제 시 관련 알림 이력도 함께 삭제 (CASCADE)
