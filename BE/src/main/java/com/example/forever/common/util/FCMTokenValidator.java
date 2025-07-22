package com.example.forever.common.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

@Slf4j
@UtilityClass
public class FCMTokenValidator {

    // FCM 토큰의 기본적인 패턴 (영문자, 숫자, 하이픈, 언더스코어, 콜론)
    private static final Pattern FCM_TOKEN_PATTERN = Pattern.compile("^[a-zA-Z0-9_:-]+$");
    
    // FCM 토큰의 일반적인 길이 범위
    private static final int MIN_TOKEN_LENGTH = 140;
    private static final int MAX_TOKEN_LENGTH = 1024;

    /**
     * FCM 토큰의 기본적인 형식 검증
     */
    public static boolean isValidFormat(String fcmToken) {
        if (fcmToken == null || fcmToken.trim().isEmpty()) {
            log.warn("FCM 토큰이 null 또는 빈 문자열입니다.");
            return false;
        }

        String trimmedToken = fcmToken.trim();

        // 길이 검증
        if (trimmedToken.length() < MIN_TOKEN_LENGTH || trimmedToken.length() > MAX_TOKEN_LENGTH) {
            log.warn("FCM 토큰 길이가 올바르지 않습니다. 길이: {}", trimmedToken.length());
            return false;
        }

        // 패턴 검증
        if (!FCM_TOKEN_PATTERN.matcher(trimmedToken).matches()) {
            log.warn("FCM 토큰 형식이 올바르지 않습니다.");
            return false;
        }

        return true;
    }

    /**
     * 토큰 마스킹 (보안을 위해 일부만 표시)
     */
    public static String maskToken(String token) {
        if (token == null || token.length() < 20) {
            return token;
        }
        return token.substring(0, 10) + "***" + token.substring(token.length() - 7);
    }

    /**
     * 디바이스 타입 검증
     */
    public static boolean isValidDeviceType(String deviceType) {
        if (deviceType == null) {
            return false;
        }
        return "ANDROID".equalsIgnoreCase(deviceType) || "IOS".equalsIgnoreCase(deviceType);
    }

    /**
     * 디바이스 ID 검증
     */
    public static boolean isValidDeviceId(String deviceId) {
        if (deviceId == null || deviceId.trim().isEmpty()) {
            return false; // 디바이스 ID는 선택사항이지만, 있다면 유효해야 함
        }
        
        String trimmedId = deviceId.trim();
        return trimmedId.length() <= 512 && !trimmedId.contains(" ");
    }
}
