package com.example.forever.dto.notification;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 푸시 알림 권한 동의 요청 DTO
 */
@Getter
@NoArgsConstructor
@Schema(description = "푸시 알림 권한 동의 요청")
public class NotificationPermissionRequest {
    
    @NotNull(message = "알림 동의 여부는 필수입니다.")
    @Schema(description = "알림 권한 동의 여부", example = "false", required = true)
    private Boolean is_agreement;
    
    public NotificationPermissionRequest(Boolean is_agreement) {
        this.is_agreement = is_agreement;
    }
    
    public Boolean getIsAgreement() {
        return is_agreement;
    }
}
