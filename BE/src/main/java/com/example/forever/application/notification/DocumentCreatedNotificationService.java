package com.example.forever.application.notification;

import com.example.forever.domain.Member;
import com.example.forever.domain.notification.NotificationType;
import com.example.forever.infrastructure.notification.FcmNotificationService;
import com.example.forever.service.NotificationQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 문서 생성 시 실시간 알림 처리 서비스
 * 사용량 마일스톤 알림을 실시간으로 체크
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentCreatedNotificationService {
    
    private final NotificationQueryService notificationQueryService;
    private final FcmNotificationService fcmNotificationService;
    
    /**
     * 문서 생성 후 마일스톤 알림 체크
     * 문서 생성 시점에 호출되어 즉시 마일스톤 달성 여부 확인
     */
    public void checkAndSendMilestoneNotification(Member member) {
        try {
            // 총 문서 개수 조회
            long totalDocuments = notificationQueryService.getTotalDocumentCount(member);
            
            log.debug("문서 생성 후 마일스톤 체크 - 회원: {}, 총 문서: {}개", 
                    member.getId(), totalDocuments);
            
            // 2개 문서 달성 시 마일스톤 알림
            if (totalDocuments == 2) {
                log.info("사용량 마일스톤 달성 - 회원: {}, 문서: {}개", 
                        member.getId(), totalDocuments);
                
                // 즉시 마일스톤 알림 발송
                boolean success = fcmNotificationService.sendNotification(
                        member, NotificationType.USAGE_MILESTONE);
                
                if (success) {
                    log.info("마일스톤 알림 발송 성공 - 회원: {}", member.getId());
                } else {
                    log.warn("마일스톤 알림 발송 실패 - 회원: {}", member.getId());
                }
            }
            
            // 향후 다른 마일스톤 추가
            
        } catch (Exception e) {
            log.error("마일스톤 알림 체크 중 오류 - 회원: {}", member.getId(), e);
        }
    }
    
    /**
     * 다양한 마일스톤 체크
     */
    public void checkAllMilestones(Member member) {
        long totalDocuments = notificationQueryService.getTotalDocumentCount(member);
        
        // 2개 달성
        if (totalDocuments == 2) {
            fcmNotificationService.sendNotification(member, NotificationType.USAGE_MILESTONE);
        }
        
        // 향후 추가 마일스톤
        // if (totalDocuments == 5) { ... }
        // if (totalDocuments == 10) { ... }
    }
}
