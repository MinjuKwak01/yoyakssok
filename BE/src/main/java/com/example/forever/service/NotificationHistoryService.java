package com.example.forever.service;

import com.example.forever.domain.Member;
import com.example.forever.domain.notification.NotificationHistory;
import com.example.forever.domain.notification.NotificationType;
import com.example.forever.repository.NotificationHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 알림 이력 관리 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationHistoryService {
    
    private final NotificationHistoryRepository notificationHistoryRepository;
    
    /**
     * 오늘 이미 해당 유형의 알림을 받았는지 확인
     */
    @Transactional(readOnly = true)
    public boolean hasReceivedTodaysNotification(Member member, NotificationType type) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        
        return notificationHistoryRepository.findTodaysSentNotification(
                member, type, startOfDay, endOfDay).isPresent();
    }
    
    /**
     * 알림 이력 생성 (발송 전)
     */
    public NotificationHistory createNotificationHistory(Member member, NotificationType type) {
        NotificationHistory history = NotificationHistory.createWithDefault(member, type);
        NotificationHistory saved = notificationHistoryRepository.save(history);
        
        log.info("알림 이력 생성 - 회원: {}, 유형: {}, ID: {}", 
                member.getId(), type, saved.getId());
        
        return saved;
    }
    
    /**
     * 커스텀 메시지로 알림 이력 생성
     */
    public NotificationHistory createNotificationHistory(Member member, NotificationType type, 
                                                        String title, String content) {
        NotificationHistory history = NotificationHistory.create(member, type, title, content);
        NotificationHistory saved = notificationHistoryRepository.save(history);
        
        log.info("커스텀 알림 이력 생성 - 회원: {}, 유형: {}, 제목: {}", 
                member.getId(), type, title);
        
        return saved;
    }
    
    /**
     * 알림 발송 성공 처리
     */
    public void markNotificationAsSent(NotificationHistory history) {
        history.markAsSent();
        notificationHistoryRepository.save(history);
        
        log.info("알림 발송 완료 - ID: {}, 회원: {}, 유형: {}", 
                history.getId(), history.getMember().getId(), history.getNotificationType());
    }
    
    /**
     * 알림 발송 실패 처리
     */
    public void markNotificationAsFailed(NotificationHistory history) {
        history.markAsFailed();
        notificationHistoryRepository.save(history);
        
        log.warn("알림 발송 실패 - ID: {}, 회원: {}, 유형: {}", 
                history.getId(), history.getMember().getId(), history.getNotificationType());
    }
    
    /**
     * 알림 발송 가능 여부 확인 (중복 방지)
     */
    @Transactional(readOnly = true)
    public boolean canSendNotification(Member member, NotificationType type) {
        // 알림 동의 여부 확인
        if (!member.getIsAgreedNotification()) {
            log.debug("알림 동의하지 않은 회원 - ID: {}", member.getId());
            return false;
        }
        
        // 오늘 이미 같은 유형의 알림을 받았는지 확인
        if (hasReceivedTodaysNotification(member, type)) {
            log.debug("오늘 이미 알림 수신한 회원 - ID: {}, 유형: {}", member.getId(), type);
            return false;
        }
        
        return true;
    }
}
