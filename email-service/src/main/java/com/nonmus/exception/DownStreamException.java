package com.nonmus.exception;

import lombok.Getter;

@Getter
public class DownStreamException extends RuntimeException {
    private final String responseBody;
    private final int status;

    public DownStreamException(int status, String responseBody) {
        this.status = status;
        this.responseBody = responseBody;
    }
}
