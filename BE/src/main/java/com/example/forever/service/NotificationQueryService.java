package com.example.forever.service;

import com.example.forever.domain.Member;
import com.example.forever.repository.DocumentRepository;
import com.example.forever.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 알림 대상 조회 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationQueryService {
    
    private final MemberRepository memberRepository;
    private final DocumentRepository documentRepository;
    
    /**
     * 문서 저장 후 1일 경과 알림 대상 조회
     * 조건: 어제 문서를 저장했지만 오늘 접속하지 않은 사용자
     */
    public List<Member> findDocumentReminderTargets() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDateTime yesterdayStart = yesterday.atStartOfDay();
        LocalDateTime yesterdayEnd = yesterday.plusDays(1).atStartOfDay();
        
        log.debug("문서 복습 알림 대상 조회: {} ~ {}", yesterdayStart, yesterdayEnd);
        
        // 어제 문서를 생성한 회원들 중에서 알림 동의한 사용자
        return memberRepository.findMembersWithDocumentsCreatedBetweenAndNotificationAgreed(
                yesterdayStart, yesterdayEnd);
    }
    
    /**
     * 요약 + 문제 2회 사용 마일스톤 달성 대상 확인
     * 조건: 총 문서 생성 횟수가 정확히 2회인 사용자
     */
    public List<Member> findUsageMilestoneTargets() {
        log.debug("사용량 마일스톤 알림 대상 조회: 총 문서 2개 생성한 사용자");
        
        // 총 문서가 정확히 2개이고 알림 동의한 사용자
        return memberRepository.findMembersWithExactDocumentCountAndNotificationAgreed(2);
    }
    
    /**
     * 특정 회원의 오늘 문서 생성 여부 확인
     */
    public boolean hasCreatedDocumentToday(Member member) {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);
        
        long count = documentRepository.countByMemberAndCreatedAtBetween(
                member, todayStart, todayEnd);
        
        return count > 0;
    }
    
    /**
     * 특정 회원의 총 문서 생성 횟수 조회
     */
    public long getTotalDocumentCount(Member member) {
        return documentRepository.countByMember(member);
    }
}
