package com.nonmus.nonmus.modules.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.nonmus.nonmus.modules.common.dto.response.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return buildErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException e) {
        return buildErrorResponse("INVALID_CREDENTIALS", "Invalid username or password.", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
       return buildErrorResponse("USER_ALREADY_EXISTS", e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException e) {
        return buildErrorResponse("USER_NOT_FOUND", e.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * File validation failure — bad MIME type or oversized file.
     * Returns 400 so the client knows it sent invalid input.
     */
    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFileException(InvalidFileException e) {
        return buildErrorResponse("INVALID_FILE", e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * S3 / storage layer failure — upstream dependency issue, not a client error.
     * Returns 502 Bad Gateway.
     */
    @ExceptionHandler(StorageException.class)
    public ResponseEntity<ErrorResponse> handleStorageException(StorageException e) {
        return buildErrorResponse("STORAGE_ERROR", "File storage operation failed. Please try again.", HttpStatus.BAD_GATEWAY);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(String errorCode, String message, HttpStatus status) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(errorCode);
        errorResponse.setMessage(message);
        return ResponseEntity.status(status).body(errorResponse);
    }
}
