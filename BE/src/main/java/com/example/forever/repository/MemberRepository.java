package com.example.forever.repository;

import com.example.forever.domain.Member;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    List<Member> findAllByIsDeletedTrueAndDeletedAtBefore(LocalDateTime time);

    /**
     * 특정 기간에 문서를 생성했지만 알림 동의한 회원 조회
     * 문서 저장 후 1일 알림용
     */
    @Query("SELECT DISTINCT m FROM Member m " +
           "JOIN Document d ON d.member = m " +
           "WHERE d.createdAt >= :startDate " +
           "AND d.createdAt < :endDate " +
           "AND m.isAgreedNotification = true " +
           "AND m.isDeleted = false")
    List<Member> findMembersWithDocumentsCreatedBetweenAndNotificationAgreed(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * 정확히 특정 개수의 문서를 생성하고 알림 동의한 회원 조회
     * 사용량 마일스톤 알림용
     */
    @Query("SELECT m FROM Member m " +
           "WHERE m.isAgreedNotification = true " +
           "AND m.isDeleted = false " +
           "AND (SELECT COUNT(d) FROM Document d WHERE d.member = m) = :documentCount")
    List<Member> findMembersWithExactDocumentCountAndNotificationAgreed(
            @Param("documentCount") long documentCount
    );
}
