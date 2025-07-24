-- 디바이스 토큰 테이블 생성
-- FCM 푸시 알림을 위한 디바이스 토큰 관리
CREATE TABLE IF NOT EXISTS device_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    device_id VARCHAR(36) NOT NULL,
    fcm_token VARCHAR(1024) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    
    CONSTRAINT fk_device_token_member 
        FOREIGN KEY (member_id) 
        REFERENCES member_tb(id) 
        ON DELETE CASCADE
);

-- 마이그레이션 로그
-- 변경 사항: 디바이스 토큰 관리를 위한 device_tokens 테이블 생성
-- 목적: FCM 푸시 알림 기능 지원
-- 필드 설명:
--   - id: 기본키 (AUTO_INCREMENT)
--   - member_id: 회원 참조 (NOT NULL, CASCADE DELETE)
--   - device_id: 디바이스 고유 식별자 (UUID 36자)
--   - fcm_token: FCM 토큰 (최대 1024자)
--   - is_active: 토큰 활성화 상태 (기본값: TRUE)
--   - created_at/updated_at: 생성/수정 시간
-- 제약 조건:
--   - 회원 삭제 시 관련 토큰도 함께 삭제 (CASCADE)
--   - UNIQUE 제약 조건 제거 (MySQL 키 길이 제한으로 인해)
-- 참고: 중복 토큰 방지는 애플리케이션 레벨에서 처리
