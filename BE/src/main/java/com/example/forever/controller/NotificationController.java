package com.example.forever.controller;

import com.example.forever.common.annotation.AuthMember;
import com.example.forever.common.annotation.MemberInfo;
import com.example.forever.common.response.ApiResponse;
import com.example.forever.common.response.ApiResponseGenerator;
import com.example.forever.dto.notification.NotificationPermissionRequest;
import com.example.forever.dto.notification.NotificationPermissionResponse;
import com.example.forever.service.NotificationPermissionService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 푸시 알림 권한 API 컨트롤러
 * 기존 AgreementController와 동일한 패턴 적용
 */
@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
@Tag(name = "푸시 알림 권한", description = "푸시 알림 권한 동의 관리 API")
public class NotificationController {
    
    private final NotificationPermissionService notificationPermissionService;
    
    @GetMapping("/permission")
    @Operation(
        summary = "푸시 알림 권한 동의 상태 조회", 
        description = "현재 사용자의 푸시 알림 수신 권한 동의 상태를 조회합니다."
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "푸시 알림 권한 상태 조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.SuccesCustomBody.class),
                examples = {
                    @ExampleObject(
                        name = "동의 상태",
                        description = "사용자가 푸시 알림을 허용한 경우",
                        value = """
                        {
                          "status": 200,
                          "message": null,
                          "data": {
                            "is_agreement": true
                          }
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "거부 상태",
                        description = "사용자가 푸시 알림을 거부한 경우",
                        value = """
                        {
                          "status": 200,
                          "message": null,
                          "data": {
                            "is_agreement": false
                          }
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
            responseCode = "404", 
            description = "존재하지 않는 회원",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "회원 없음",
                    value = """
                    {
                      "status": 404,
                      "message": "존재하지 않는 회원입니다.",
                      "data": null
                    }
                    """
                )
            )
        )
    })
    public ApiResponse<ApiResponse.SuccesCustomBody<NotificationPermissionResponse>> getNotificationPermission(
            @Parameter(hidden = true) @AuthMember MemberInfo memberInfo) {
        
        NotificationPermissionResponse response = notificationPermissionService
            .getNotificationPermission(memberInfo);
        
        return ApiResponseGenerator.success(response, HttpStatus.OK);
    }
    
    @PostMapping("/permission")
    @Operation(
        summary = "푸시 알림 권한 동의 설정", 
        description = "사용자의 푸시 알림 수신 권한 동의 여부를 설정합니다. " +
                     "true로 설정하면 푸시 알림을 수신하고, false로 설정하면 푸시 알림을 수신하지 않습니다."
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "푸시 알림 권한 설정 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.SuccesCustomBody.class),
                examples = @ExampleObject(
                    name = "성공 응답",
                    value = """
                    {
                      "status": 200,
                      "message": null,
                      "data": null
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", 
            description = "요청 데이터가 유효하지 않음",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "유효성 검사 실패",
                    value = """
                    {
                      "status": 400,
                      "message": "알림 동의 여부는 필수입니다.",
                      "data": null
                    }
                    """
                )
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
            responseCode = "404", 
            description = "존재하지 않는 회원",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "회원 없음",
                    value = """
                    {
                      "status": 404,
                      "message": "존재하지 않는 회원입니다.",
                      "data": null
                    }
                    """
                )
            )
        )
    })
    public ApiResponse<ApiResponse.SuccesCustomBody<Void>> updateNotificationPermission(
            @Parameter(hidden = true) @AuthMember MemberInfo memberInfo,
            @Parameter(
                description = "푸시 알림 권한 동의 요청", 
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = NotificationPermissionRequest.class),
                    examples = {
                        @ExampleObject(
                            name = "동의",
                            description = "푸시 알림 수신에 동의하는 경우",
                            value = """
                            {
                              "is_agreement": true
                            }
                            """
                        ),
                        @ExampleObject(
                            name = "거부",  
                            description = "푸시 알림 수신을 거부하는 경우",
                            value = """
                            {
                              "is_agreement": false
                            }
                            """
                        )
                    }
                )
            ) @Valid @RequestBody NotificationPermissionRequest request) {
        
        notificationPermissionService.updateNotificationPermission(
            memberInfo, 
            request.getIsAgreement()
        );
        
        return ApiResponseGenerator.success(HttpStatus.OK);
    }
}
