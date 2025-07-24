package com.example.forever.infrastructure.scheduler;

import com.example.forever.domain.Member;
import com.example.forever.domain.notification.NotificationType;
import com.example.forever.infrastructure.notification.FcmNotificationService;
import com.example.forever.service.NotificationQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 푸시 알림 스케줄러
 * 시간 기반 알림 발송을 담당
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {
    
    private final NotificationQueryService notificationQueryService;
    private final FcmNotificationService fcmNotificationService;
    
    /**
     * 문서 저장 후 1일 경과 알림
     * 매일 오후 6시에 실행
     */
    @Scheduled(cron = "0 0 18 * * *")
    public void sendDocumentReminderNotifications() {
        log.info("=== 문서 복습 알림 스케줄 시작 ===");
        
        try {
            // 알림 대상 조회: 어제 문서를 저장했지만 오늘 접속하지 않은 사용자
            List<Member> targets = notificationQueryService.findDocumentReminderTargets();
            
            if (targets.isEmpty()) {
                log.info("문서 복습 알림 대상이 없습니다.");
                return;
            }
            
            log.info("문서 복습 알림 대상: {}명", targets.size());
            
            // 일괄 알림 발송
            fcmNotificationService.sendBatchNotifications(targets, NotificationType.DOCUMENT_REMINDER);
            
        } catch (Exception e) {
            log.error("문서 복습 알림 스케줄 실행 중 오류 발생", e);
        } finally {
            log.info("=== 문서 복습 알림 스케줄 종료 ===");
        }
    }
    
    /**
     * 사용량 마일스톤 알림 체크
     * 매시간 실행하여 새로운 마일스톤 달성자 확인
     */
    @Scheduled(cron = "0 0 * * * *")
    public void checkUsageMilestoneNotifications() {
        log.info("=== 사용량 마일스톤 알림 체크 시작 ===");
        
        try {
            // 알림 대상 조회: 총 문서가 정확히 2개이고 알림 동의한 사용자
            List<Member> targets = notificationQueryService.findUsageMilestoneTargets();
            
            if (targets.isEmpty()) {
                log.debug("사용량 마일스톤 알림 대상이 없습니다.");
                return;
            }
            
            log.info("사용량 마일스톤 알림 대상: {}명", targets.size());
            
            // 일괄 알림 발송
            fcmNotificationService.sendBatchNotifications(targets, NotificationType.USAGE_MILESTONE);
            
        } catch (Exception e) {
            log.error("사용량 마일스톤 알림 체크 중 오류 발생", e);
        } finally {
            log.debug("=== 사용량 마일스톤 알림 체크 종료 ===");
        }
    }
}
