package com.nonmus.exception;

public class DownStreamServiceException extends RuntimeException {
    private String code;
    private Exception actualException;

    public DownStreamServiceException(String code, String message, Exception actualException) {
        super(message);
        this.code = code;
        this.actualException = actualException;
    }

    public String getCode() {
        return code;
    }
    
    public Exception getActualException() {
        return actualException;
    }
}
