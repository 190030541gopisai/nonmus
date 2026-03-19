package com.nonmus.exception;

import lombok.Getter;

@Getter
public class EmailServiceException extends RuntimeException {
    private final String responseBody;
    private final int status;

    public EmailServiceException(int status, String responseBody) {
        this.status = status;
        this.responseBody = responseBody;
    }
}
