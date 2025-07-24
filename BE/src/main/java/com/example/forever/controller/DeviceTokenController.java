package com.example.forever.controller;

import com.example.forever.application.notification.RegisterDeviceTokenUseCase;
import com.example.forever.common.annotation.AuthMember;
import com.example.forever.common.annotation.MemberInfo;
import com.example.forever.common.response.ApiResponse;
import com.example.forever.common.response.ApiResponseGenerator;
import com.example.forever.domain.Member;
import com.example.forever.dto.notification.DeviceTokenRegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 디바이스 토큰 API 컨트롤러
 * DDD Interface Layer 패턴 적용
 */
@RestController
@RequestMapping("/api/device-tokens")
@RequiredArgsConstructor
public class DeviceTokenController {
    
    private final RegisterDeviceTokenUseCase registerDeviceTokenUseCase;
    
    @PostMapping("/register")
    public ApiResponse<ApiResponse.SuccesCustomBody<Void>> registerDeviceToken(
            @Valid @RequestBody DeviceTokenRegisterRequest request,
            @AuthMember MemberInfo member) {
        
        registerDeviceTokenUseCase.execute(member, request.getFcmToken());
        
        return ApiResponseGenerator.success(
            "디바이스 토큰이 성공적으로 등록되었습니다.", 
            HttpStatus.OK
        );
    }
}
