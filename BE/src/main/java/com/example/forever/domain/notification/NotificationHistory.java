package com.example.forever.domain.notification;

import com.example.forever.domain.BaseTimeEntity;
import com.example.forever.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 알림 발송 이력 엔티티
 */
@Entity
@Table(name = "notification_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationHistory extends BaseTimeEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", length = 50, nullable = false)
    private NotificationType notificationType;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Column(name = "is_sent", nullable = false)
    private boolean sent = false;
    
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
    
    private NotificationHistory(Member member, NotificationType notificationType, 
                               String title, String content) {
        this.member = member;
        this.notificationType = notificationType;
        this.title = title;
        this.content = content;
        this.sent = false;
    }
    
    /**
     * 알림 이력 생성
     */
    public static NotificationHistory create(Member member, NotificationType type, 
                                           String title, String content) {
        return new NotificationHistory(member, type, title, content);
    }
    
    /**
     * 기본 메시지로 알림 이력 생성
     */
    public static NotificationHistory createWithDefault(Member member, NotificationType type) {
        return new NotificationHistory(member, type, type.getTitle(), type.getDefaultMessage());
    }
    
    /**
     * 발송 완료 처리
     */
    public void markAsSent() {
        this.sent = true;
        this.sentAt = LocalDateTime.now();
    }
    
    /**
     * 발송 실패 처리
     */
    public void markAsFailed() {
        this.sent = false;
        this.sentAt = null;
    }
    
    /**
     * 오늘 발송된 알림인지 확인
     */
    public boolean isSentToday() {
        if (sentAt == null) {
            return false;
        }
        return sentAt.toLocalDate().equals(LocalDateTime.now().toLocalDate());
    }
    
    /**
     * 특정 회원의 특정 유형 알림인지 확인
     */
    public boolean isForMemberAndType(Member member, NotificationType type) {
        return this.member.equals(member) && this.notificationType.equals(type);
    }
}
