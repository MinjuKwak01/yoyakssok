package com.example.forever.repository;

import com.example.forever.domain.Member;
import com.example.forever.domain.notification.NotificationHistory;
import com.example.forever.domain.notification.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 알림 이력 리포지토리
 */
@Repository
public interface NotificationHistoryRepository extends JpaRepository<NotificationHistory, Long> {
    
    /**
     * 특정 회원의 특정 유형 알림이 오늘 발송되었는지 확인
     */
    @Query("SELECT nh FROM NotificationHistory nh " +
           "WHERE nh.member = :member " +
           "AND nh.notificationType = :type " +
           "AND nh.sent = true " +
           "AND nh.sentAt >= :startOfDay " +
           "AND nh.sentAt < :endOfDay")
    Optional<NotificationHistory> findTodaysSentNotification(
            @Param("member") Member member,
            @Param("type") NotificationType type,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );
    
    /**
     * 특정 회원의 알림 이력 조회 (최신순)
     */
    @Query("SELECT nh FROM NotificationHistory nh " +
           "WHERE nh.member = :member " +
           "ORDER BY nh.createdAt DESC")
    List<NotificationHistory> findByMemberOrderByCreatedAtDesc(@Param("member") Member member);
    
    /**
     * 발송 대기중인 알림 조회
     */
    @Query("SELECT nh FROM NotificationHistory nh " +
           "WHERE nh.sent = false " +
           "ORDER BY nh.createdAt ASC")
    List<NotificationHistory> findPendingNotifications();
    
    /**
     * 특정 기간 동안의 발송된 알림 통계
     */
    @Query("SELECT nh.notificationType, COUNT(nh) " +
           "FROM NotificationHistory nh " +
           "WHERE nh.sent = true " +
           "AND nh.sentAt >= :startDate " +
           "AND nh.sentAt < :endDate " +
           "GROUP BY nh.notificationType")
    List<Object[]> getNotificationStatistics(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
