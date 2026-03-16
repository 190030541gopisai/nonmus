package com.nonmus.exception;

import lombok.Getter;

@Getter
public class DownStreamServiceException extends RuntimeException {

    private final String code;
    private final int statusCode;

    public DownStreamServiceException(String code, String message, int statusCode) {
        super(message);
        this.code = code;
        this.statusCode = statusCode;
    }
}
