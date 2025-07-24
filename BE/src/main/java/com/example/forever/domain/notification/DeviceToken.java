package com.example.forever.domain.notification;

import com.example.forever.domain.BaseTimeEntity;
import com.example.forever.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 디바이스 토큰 엔티티
 * DDD Entity 패턴 적용
 */
@Entity
@Table(name = "device_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeviceToken extends BaseTimeEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Embedded
    private DeviceId deviceId;
    
    @Embedded
    private FcmToken fcmToken;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    
    @Column(name = "is_active", nullable = false)
    private boolean active = true;
    
    private DeviceToken(Member member, FcmToken fcmToken, DeviceId deviceId) {
        this.member = member;
        this.fcmToken = fcmToken;
        this.deviceId = deviceId;
        this.active = true;
    }
    
    public static DeviceToken create(Member member, FcmToken fcmToken) {
        return new DeviceToken(member, fcmToken, DeviceId.generate());
    }
    
    public void updateToken(FcmToken newToken) {
        this.fcmToken = newToken;
        activate();
    }
    
    public void activate() {
        this.active = true;
    }
    
    public void deactivate() {
        this.active = false;
    }
    
    public boolean isSameToken(FcmToken token) {
        return this.fcmToken.equals(token);
    }
    
    public boolean belongsTo(Member member) {
        return this.member.equals(member);
    }
}
