-- 푸시 알림 권한 동의 필드 추가
-- 회원 테이블에 알림 동의 관련 컬럼 추가
ALTER TABLE member_tb 
ADD COLUMN is_agreed_notification BOOLEAN NOT NULL DEFAULT FALSE,
ADD COLUMN effective_date_notification DATE DEFAULT NULL;

-- 마이그레이션 로그
-- 변경 사항: 푸시 알림 권한 동의 관리를 위한 필드 추가
-- 목적: 사용자별 푸시 알림 수신 동의 여부 관리
-- 필드 설명:
--   - is_agreed_notification: 푸시 알림 수신 동의 여부 (기본값: FALSE)
--   - effective_date_notification: 동의/거부 설정 일시 (NULL 허용)
-- 적용 일시: 2025년 7월 24일
