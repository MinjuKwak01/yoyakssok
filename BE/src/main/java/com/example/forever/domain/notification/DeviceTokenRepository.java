package com.example.forever.domain.notification;

import com.example.forever.domain.Member;

import java.util.List;
import java.util.Optional;

/**
 * 디바이스 토큰 도메인 리포지토리 인터페이스
 * DDD Repository 패턴 적용
 */
public interface DeviceTokenRepository {
    
    DeviceToken save(DeviceToken deviceToken);
    
    Optional<DeviceToken> findByMemberAndFcmToken(Member member, FcmToken fcmToken);
    
    List<DeviceToken> findActiveTokensByMember(Member member);
    
    void delete(DeviceToken deviceToken);
    
    void deleteAll();
}
