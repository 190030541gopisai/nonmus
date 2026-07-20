package com.nonmus.nonmus.modules.common.exception;

/**
 * Thrown when an uploaded file fails validation — either the MIME type is not
 * on the allowlist or the file size exceeds the configured maximum.
 *
 * Maps to HTTP 400 Bad Request via GlobalExceptionHandler.
 */
public class InvalidFileException extends RuntimeException {

    public InvalidFileException(String message) {
        super(message);
    }
}
