package com.nonmus.exception;

public class UserServiceException extends RuntimeException {
    private final String code;

    public UserServiceException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }    
}
