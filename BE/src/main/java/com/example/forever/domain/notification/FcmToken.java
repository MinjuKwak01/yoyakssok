package com.example.forever.domain.notification;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * FCM 토큰 값 객체
 * DDD Value Object 패턴 적용
 */
@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FcmToken {
    
    private static final int MAX_TOKEN_LENGTH = 1024;
    
    @Column(name = "fcm_token", length = MAX_TOKEN_LENGTH, nullable = false)
    private String value;
    
    private FcmToken(String value) {
        this.value = value;
    }
    
    public static FcmToken of(String token) {
        validateToken(token);
        return new FcmToken(token);
    }
    
    private static void validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("FCM 토큰은 필수입니다.");
        }
        
        if (token.length() > MAX_TOKEN_LENGTH) {
            throw new IllegalArgumentException("FCM 토큰은 " + MAX_TOKEN_LENGTH + "자를 초과할 수 없습니다.");
        }
    }
    
    @Override
    public String toString() {
        return value;
    }
}
