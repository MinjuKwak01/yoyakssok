package com.example.forever.domain.notification;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * 디바이스 식별자 값 객체
 * DDD Value Object 패턴 적용
 */
@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeviceId {
    
    @Column(name = "device_id", length = 36, nullable = false)
    private String value;
    
    private DeviceId(String value) {
        this.value = value;
    }
    
    public static DeviceId generate() {
        return new DeviceId(UUID.randomUUID().toString());
    }
    
    public static DeviceId of(String deviceId) {
        if (deviceId == null || deviceId.trim().isEmpty()) {
            throw new IllegalArgumentException("디바이스 ID는 필수입니다.");
        }
        return new DeviceId(deviceId);
    }
    
    @Override
    public String toString() {
        return value;
    }
}
