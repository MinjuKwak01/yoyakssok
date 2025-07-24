package com.example.forever.dto.notification;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 푸시 알림 권한 동의 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "푸시 알림 권한 동의 응답")
public class NotificationPermissionResponse {
    
    @Schema(description = "알림 권한 동의 여부", example = "false")
    private Boolean is_agreement;
    
    public static NotificationPermissionResponse of(Boolean isAgreed) {
        return new NotificationPermissionResponse(isAgreed);
    }
}
