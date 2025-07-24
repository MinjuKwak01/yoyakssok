package com.example.forever.controller;

import com.example.forever.application.notification.RegisterDeviceTokenUseCase;
import com.example.forever.common.annotation.MemberInfo;
import com.example.forever.common.response.ApiResponse;
import com.example.forever.common.response.ApiResponseGenerator;
import com.example.forever.domain.Member;
import com.example.forever.dto.notification.DeviceTokenRegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "디바이스 토큰", description = "FCM 푸시 알림을 위한 디바이스 토큰 관리 API")
public class DeviceTokenController {
    
    private final RegisterDeviceTokenUseCase registerDeviceTokenUseCase;
    
    @PostMapping("/register")
    @Operation(
        summary = "디바이스 토큰 등록", 
        description = "FCM 푸시 알림을 위한 디바이스 토큰을 등록합니다. " +
                     "동일한 토큰이 이미 등록되어 있으면 기존 토큰을 업데이트합니다."
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "디바이스 토큰 등록 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.SuccesCustomBody.class),
                examples = @ExampleObject(
                    name = "성공 응답",
                    value = """
                    {
                      "status": 200,
                      "message": "디바이스 토큰이 성공적으로 등록되었습니다.",
                      "data": null
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", 
            description = "잘못된 요청 데이터",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "토큰 누락",
                        value = """
                        {
                          "status": 400,
                          "message": "FCM 토큰은 필수입니다.",
                          "data": null
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "토큰 길이 초과",
                        value = """
                        {
                          "status": 400,
                          "message": "FCM 토큰은 1024자를 초과할 수 없습니다.",
                          "data": null
                        }
                        """
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401", 
            description = "인증되지 않은 사용자",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "인증 실패",
                    value = """
                    {
                      "status": 401,
                      "message": "인증이 필요합니다.",
                      "data": null
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500", 
            description = "서버 내부 오류",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "서버 오류",
                    value = """
                    {
                      "status": 500,
                      "message": "서버 내부 오류가 발생했습니다.",
                      "data": null
                    }
                    """
                )
            )
        )
    })
    public ApiResponse<ApiResponse.SuccesCustomBody<Void>> registerDeviceToken(
            @Parameter(
                description = "디바이스 토큰 등록 요청", 
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DeviceTokenRegisterRequest.class),
                    examples = @ExampleObject(
                        name = "FCM 토큰 등록",
                        description = "Firebase FCM 토큰을 등록하는 예시",
                        value = """
                        {
                          "fcmToken": "dGhpcyBpcyBhIGZha2UgZmNtIHRva2VuIGZvciBleGFtcGxlIHB1cnBvc2VzIG9ubHk"
                        }
                        """
                    )
                )
            ) @Valid @RequestBody DeviceTokenRegisterRequest request,
            MemberInfo currentMember) { // JWT 인증을 통해 주입된다고 가정
        
        registerDeviceTokenUseCase.execute(currentMember, request.getFcmToken());
        
        return ApiResponseGenerator.success(
            "디바이스 토큰이 성공적으로 등록되었습니다.", 
            HttpStatus.OK
        );
    }
}
