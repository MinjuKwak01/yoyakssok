package com.example.forever.domain.notification;

/**
 * 푸시 알림 유형
 * DDD Value Object 패턴 적용
 */
public enum NotificationType {
    
    /**
     * 문서 저장 후 1일 복습 알림
     */
    DOCUMENT_REMINDER("문서 복습 알림", "잊으면 다 사라지니다... 어제 문제 지금 복습 안 하면 끝!"),
    
    /**
     * 요약 + 문제 2회 사용 마일스톤 알림
     */
    USAGE_MILESTONE("사용량 마일스톤 알림", "요약왕에 도전해보세요!"),
    
    /**
     * 3일 이상 미사용 알림 (향후 확장용)
     */
    INACTIVE_USER("비활성 사용자 알림", "이거 까먹으면 안 됨 초... 요약 정리본 바로 확인!"),
    
    /**
     * 앱 설치 후 7일 경과 알림 (향후 확장용)
     */
    WEEKLY_REMINDER("주간 리마인더", "벌써 일주일! 이렇게 포기할 건 아니죠?");
    
    private final String displayName;
    private final String defaultMessage;
    
    NotificationType(String displayName, String defaultMessage) {
        this.displayName = displayName;
        this.defaultMessage = defaultMessage;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDefaultMessage() {
        return defaultMessage;
    }
    
    /**
     * 알림 제목 생성
     */
    public String getTitle() {
        return switch (this) {
            case DOCUMENT_REMINDER -> "📚 복습 시간이에요!";
            case USAGE_MILESTONE -> "🎉 축하합니다!";
            case INACTIVE_USER -> "💡 잊지 마세요!";
            case WEEKLY_REMINDER -> "⏰ 일주일이 지났어요!";
        };
    }
}
