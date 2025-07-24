package com.example.forever.service;

import com.example.forever.common.annotation.MemberInfo;
import com.example.forever.common.validator.MemberValidator;
import com.example.forever.domain.Member;
import com.example.forever.dto.notification.NotificationPermissionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 푸시 알림 권한 동의 서비스
 * 기존 AgreementService와 동일한 패턴 적용
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationPermissionService {
    
    private final MemberValidator memberValidator;
    
    /**
     * 푸시 알림 권한 동의 상태 변경
     * 
     * @param memberInfo 회원 정보
     * @param isAgreed 동의 여부
     */
    public void updateNotificationPermission(MemberInfo memberInfo, boolean isAgreed) {
        Member member = memberValidator.validateAndGetById(memberInfo.getMemberId());
        member.updateNotificationAgreement(isAgreed);
        
        log.info("회원 {}의 푸시 알림 권한이 {}로 변경되었습니다.", 
                memberInfo.getMemberId(), 
                isAgreed ? "동의" : "거부");
    }
    
    /**
     * 푸시 알림 권한 동의 상태 조회
     * 
     * @param memberInfo 회원 정보
     * @return 알림 권한 동의 응답
     */
    @Transactional(readOnly = true)
    public NotificationPermissionResponse getNotificationPermission(MemberInfo memberInfo) {
        Member member = memberValidator.validateAndGetById(memberInfo.getMemberId());
        return NotificationPermissionResponse.of(member.getIsAgreedNotification());
    }
}
