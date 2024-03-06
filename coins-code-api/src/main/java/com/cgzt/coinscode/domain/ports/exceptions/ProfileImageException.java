package com.cgzt.coinscode.domain.ports.exceptions;

public class ProfileImageException extends RuntimeException {
    public ProfileImageException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
