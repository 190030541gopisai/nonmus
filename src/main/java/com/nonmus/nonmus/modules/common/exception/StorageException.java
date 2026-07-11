package com.nonmus.nonmus.modules.common.exception;

/**
 * Thrown when an S3 / storage layer operation fails at runtime — e.g. network error,
 * access denied on the bucket, or an unexpected SDK exception.
 *
 * Maps to HTTP 502 Bad Gateway via GlobalExceptionHandler, since this is an
 * upstream dependency failure, not a client input error.
 */
public class StorageException extends RuntimeException {

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
