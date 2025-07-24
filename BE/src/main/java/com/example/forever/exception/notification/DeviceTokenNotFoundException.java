package com.example.forever.exception.notification;

/**
 * 디바이스 토큰을 찾을 수 없을 때 발생하는 예외
 */
public class DeviceTokenNotFoundException extends RuntimeException {
    
    public DeviceTokenNotFoundException(String message) {
        super(message);
    }
    
    public DeviceTokenNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
