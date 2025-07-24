package com.example.forever.infrastructure.notification;

/**
 * 알림 전략 인터페이스
 * OCP(Open-Closed Principle) 적용: 새로운 알림 방식 추가시 기존 코드 수정 없이 확장 가능
 */
public interface NotificationStrategy {
    
    void send(NotificationMessage message);
    
    boolean supports(NotificationType type);
    
    enum NotificationType {
        FCM, APNS, EMAIL, SMS
    }
}
