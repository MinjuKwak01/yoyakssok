package com.example.forever.domain.notification;

import com.example.forever.common.annotation.MemberInfo;
import com.example.forever.domain.Member;
import com.example.forever.exception.auth.MemberNotFoundException;
import com.example.forever.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 디바이스 토큰 도메인 서비스
 * DDD Domain Service 패턴 적용
 */
@Service
@RequiredArgsConstructor
public class DeviceTokenDomainService {
    
    private final DeviceTokenRepository deviceTokenRepository;
    private final MemberRepository memberRepository;

    /**
     * 디바이스 토큰 등록 또는 업데이트
     * 비즈니스 규칙: 동일한 토큰이 존재하면 업데이트, 없으면 새로 생성
     */
    public DeviceToken registerOrUpdateToken(MemberInfo memberInfo, FcmToken fcmToken) {
        Member member = memberRepository.findById(memberInfo.getMemberId()).orElseThrow(
                ()-> new MemberNotFoundException("존재하지 않는 회원입니다.")
        );
        return deviceTokenRepository.findByMemberAndFcmToken(member, fcmToken)
                .map(existingToken -> {
                    existingToken.updateToken(fcmToken);
                    return deviceTokenRepository.save(existingToken);
                })
                .orElseGet(() -> {
                    DeviceToken newToken = DeviceToken.create(member, fcmToken);
                    return deviceTokenRepository.save(newToken);
                });
    }
    
    /**
     * 사용자의 활성 토큰 조회
     */
    public java.util.List<DeviceToken> getActiveTokens(Member member) {
        return deviceTokenRepository.findActiveTokensByMember(member);
    }
}
