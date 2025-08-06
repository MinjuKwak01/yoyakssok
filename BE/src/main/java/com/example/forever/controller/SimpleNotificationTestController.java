package com.example.forever.controller;

import com.example.forever.common.annotation.AuthMember;
import com.example.forever.common.annotation.MemberInfo;
import com.example.forever.common.response.ApiResponse;
import com.example.forever.common.response.ApiResponseGenerator;
import com.example.forever.common.validator.MemberValidator;
import com.example.forever.domain.Member;
import com.example.forever.domain.notification.NotificationType;
import com.example.forever.infrastructure.notification.FcmNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * 간단한 테스트 알림 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class SimpleNotificationTestController {
    
    private final FcmNotificationService fcmNotificationService;
    private final MemberValidator memberValidator;
    
    @PostMapping("/notification")
    public ApiResponse<ApiResponse.SuccesCustomBody<Void>> sendTestNotification(
            @AuthMember MemberInfo memberInfo) {
        
        Member member = memberValidator.validateAndGetById(memberInfo.getMemberId());
        
        log.info("🧪 테스트 알림 발송 - 회원 ID: {}", member.getId());
        
        // 간단하게 기존 NotificationType을 사용
        boolean success = fcmNotificationService.sendNotification(member, NotificationType.USAGE_MILESTONE);
        
        if (success) {
            log.info("✅ 테스트 알림 발송 성공");
            return ApiResponseGenerator.success("테스트 알림 발송 성공!", HttpStatus.OK);
        } else {
            log.warn("❌ 테스트 알림 발송 실패");
            return ApiResponseGenerator.success("테스트 알림 발송 실패", HttpStatus.BAD_REQUEST);
        }
    }
}
