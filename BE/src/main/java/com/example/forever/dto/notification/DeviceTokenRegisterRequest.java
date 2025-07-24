package com.example.forever.dto.notification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 디바이스 토큰 등록 요청 DTO
 */
@Getter
@NoArgsConstructor
public class DeviceTokenRegisterRequest {
    
    @NotBlank(message = "FCM 토큰은 필수입니다.")
    @Size(max = 1024, message = "FCM 토큰은 1024자를 초과할 수 없습니다.")
    private String fcmToken;
    
    public DeviceTokenRegisterRequest(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
