package com.example.forever.infrastructure.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 알림 메시지 값 객체
 */
@Getter
@AllArgsConstructor
public class NotificationMessage {
    
    private final String title;
    private final String content;
    private final String recipient;
    private final NotificationStrategy.NotificationType type;
    
    public static NotificationMessage fcm(String title, String content, String fcmToken) {
        return new NotificationMessage(title, content, fcmToken, NotificationStrategy.NotificationType.FCM);
    }
}
