-- 일일 토큰 사용량을 100개로 변경
UPDATE member_tb 
SET available_tokens = 100 
WHERE is_deleted = FALSE;

-- 마이그레이션 로그
-- 변경 사항: 일일 토큰 사용량 3개 → 100개로 증가
-- 적용 대상: 삭제되지 않은 모든 활성 회원
-- 적용 일시: 2025년 6월 27일
