package com.example.forever.exception.notification;

/**
 * 유효하지 않은 FCM 토큰 예외
 */
public class InvalidFcmTokenException extends RuntimeException {
    
    public InvalidFcmTokenException(String message) {
        super(message);
    }
    
    public InvalidFcmTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
