package com.example.forever.infrastructure.notification;

import com.example.forever.domain.Member;
import com.example.forever.domain.notification.DeviceToken;
import com.example.forever.domain.notification.DeviceTokenDomainService;
import com.example.forever.domain.notification.NotificationHistory;
import com.example.forever.domain.notification.NotificationType;
import com.example.forever.service.NotificationHistoryService;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * FCM 푸시 알림 발송 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FcmNotificationService {
    
    private final DeviceTokenDomainService deviceTokenDomainService;
    private final NotificationHistoryService notificationHistoryService;
    
    /**
     * 단일 사용자에게 알림 발송
     */
    public boolean sendNotification(Member member, NotificationType type) {
        // 발송 가능 여부 확인
        if (!notificationHistoryService.canSendNotification(member, type)) {
            return false;
        }
        
        // 알림 이력 생성
        NotificationHistory history = notificationHistoryService
                .createNotificationHistory(member, type);
        
        try {
            // 활성 디바이스 토큰 조회
            List<DeviceToken> activeTokens = deviceTokenDomainService.getActiveTokens(member);
            
            if (activeTokens.isEmpty()) {
                log.warn("활성 디바이스 토큰이 없는 회원 - ID: {}", member.getId());
                notificationHistoryService.markNotificationAsFailed(history);
                return false;
            }
            
            // FCM 메시지 생성
            String title = type.getTitle();
            String content = type.getDefaultMessage();
            
            // 여러 디바이스에 발송
            boolean success = sendToMultipleDevices(activeTokens, title, content);
            
            if (success) {
                notificationHistoryService.markNotificationAsSent(history);
                log.info("알림 발송 성공 - 회원: {}, 유형: {}, 디바이스 수: {}", 
                        member.getId(), type, activeTokens.size());
            } else {
                notificationHistoryService.markNotificationAsFailed(history);
                log.error("알림 발송 실패 - 회원: {}, 유형: {}", member.getId(), type);
            }
            
            return success;
            
        } catch (Exception e) {
            log.error("알림 발송 중 오류 발생 - 회원: {}, 유형: {}", member.getId(), type, e);
            notificationHistoryService.markNotificationAsFailed(history);
            return false;
        }
    }
    
    /**
     * 여러 사용자에게 일괄 알림 발송
     */
    public void sendBatchNotifications(List<Member> members, NotificationType type) {
        log.info("일괄 알림 발송 시작 - 대상: {}명, 유형: {}", members.size(), type);
        
        int successCount = 0;
        int failCount = 0;
        
        for (Member member : members) {
            try {
                boolean success = sendNotification(member, type);
                if (success) {
                    successCount++;
                } else {
                    failCount++;
                }
                
                // API 호출 제한 방지를 위한 잠시 대기
                Thread.sleep(50);
                
            } catch (Exception e) {
                log.error("회원 {}에게 알림 발송 실패", member.getId(), e);
                failCount++;
            }
        }
        
        log.info("일괄 알림 발송 완료 - 성공: {}건, 실패: {}건, 유형: {}", 
                successCount, failCount, type);
    }
    
    /**
     * 여러 디바이스에 동시 발송
     */
    private boolean sendToMultipleDevices(List<DeviceToken> deviceTokens, 
                                        String title, String content) {
        try {
            if (deviceTokens.size() == 1) {
                // 단일 디바이스 발송
                return sendToSingleDevice(deviceTokens.get(0), title, content);
            } else {
                // 멀티캐스트 발송
                return sendMulticast(deviceTokens, title, content);
            }
        } catch (Exception e) {
            log.error("FCM 발송 중 오류", e);
            return false;
        }
    }
    
    /**
     * 단일 디바이스 발송
     */
    private boolean sendToSingleDevice(DeviceToken deviceToken, String title, String content) {
        try {
            Message message = Message.builder()
                    .setToken(deviceToken.getFcmToken().getValue())
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(content)
                            .build())
                    .setAndroidConfig(AndroidConfig.builder()
                            .setNotification(AndroidNotification.builder()
                                    .setIcon("ic_notification")
                                    .setColor("#FF6B35")
                                    .build())
                            .build())
                    .setApnsConfig(ApnsConfig.builder()
                            .setAps(Aps.builder()
                                    .setBadge(1)
                                    .setSound("default")
                                    .build())
                            .build())
                    .build();
            
            String response = FirebaseMessaging.getInstance().send(message);
            log.debug("FCM 단일 발송 성공 - Response: {}", response);
            return true;
            
        } catch (FirebaseMessagingException e) {
            log.error("FCM 단일 발송 실패 - 토큰: {}", 
                    deviceToken.getFcmToken().getValue().substring(0, 20) + "...", e);
            return false;
        }
    }
    
    /**
     * 멀티캐스트 발송
     */
    private boolean sendMulticast(List<DeviceToken> deviceTokens, String title, String content) {
        try {
            List<String> tokens = deviceTokens.stream()
                    .map(dt -> dt.getFcmToken().getValue())
                    .toList();
            
            MulticastMessage message = MulticastMessage.builder()
                    .addAllTokens(tokens)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(content)
                            .build())
                    .setAndroidConfig(AndroidConfig.builder()
                            .setNotification(AndroidNotification.builder()
                                    .setIcon("ic_notification")
                                    .setColor("#FF6B35")
                                    .build())
                            .build())
                    .setApnsConfig(ApnsConfig.builder()
                            .setAps(Aps.builder()
                                    .setBadge(1)
                                    .setSound("default")
                                    .build())
                            .build())
                    .build();
            
            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
            
            log.debug("FCM 멀티캐스트 발송 완료 - 성공: {}, 실패: {}", 
                    response.getSuccessCount(), response.getFailureCount());
            
            // 50% 이상 성공하면 성공으로 간주
            return response.getSuccessCount() >= (tokens.size() / 2);
            
        } catch (FirebaseMessagingException e) {
            log.error("FCM 멀티캐스트 발송 실패", e);
            return false;
        }
    }
}
