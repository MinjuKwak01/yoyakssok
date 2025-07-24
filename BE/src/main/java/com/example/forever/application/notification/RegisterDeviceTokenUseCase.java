package com.example.forever.application.notification;

import com.example.forever.common.annotation.MemberInfo;
import com.example.forever.domain.Member;
import com.example.forever.domain.notification.DeviceToken;
import com.example.forever.domain.notification.DeviceTokenDomainService;
import com.example.forever.domain.notification.FcmToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 디바이스 토큰 등록 유스케이스
 * DDD Application Service 패턴 적용
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RegisterDeviceTokenUseCase {
    
    private final DeviceTokenDomainService deviceTokenDomainService;
    
    public void execute(MemberInfo member, String fcmTokenValue) {
        FcmToken fcmToken = FcmToken.of(fcmTokenValue);
        DeviceToken token = deviceTokenDomainService.registerOrUpdateToken(member, fcmToken);
    }
}
