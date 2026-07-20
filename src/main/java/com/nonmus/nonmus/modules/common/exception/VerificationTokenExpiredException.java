package com.nonmus.nonmus.modules.common.exception;

public class VerificationTokenExpiredException extends RuntimeException {
    public VerificationTokenExpiredException(String message) {
        super(message);
    }
}
