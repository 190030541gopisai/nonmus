package com.nonmus.exception;

import lombok.Getter;

@Getter
public class UserServiceException extends RuntimeException {

    private final String code;
    private final int statusCode;

    public UserServiceException(String code, String message, int statusCode) {
        super(message);
        this.code = code;
        this.statusCode = statusCode;
    }
}
