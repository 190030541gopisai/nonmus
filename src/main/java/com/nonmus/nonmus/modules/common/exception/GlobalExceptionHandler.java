package com.nonmus.nonmus.modules.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.nonmus.nonmus.modules.common.dto.response.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unhandled exception", e);
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

    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFileException(InvalidFileException e) {
        return buildErrorResponse("INVALID_FILE", e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<ErrorResponse> handleStorageException(StorageException e) {
        return buildErrorResponse("STORAGE_ERROR", "File storage operation failed. Please try again.", HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(InvalidVerificationTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidVerificationTokenException(
            InvalidVerificationTokenException e) {

        return buildErrorResponse(
                "INVALID_VERIFICATION_TOKEN",
                e.getMessage(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(OtpExpiredException.class)
    public ResponseEntity<ErrorResponse> handleOtpExpiredException(
            OtpExpiredException e) {
        return buildErrorResponse(
                "OTP_EXPIRED",
                e.getMessage(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<ErrorResponse> handleOtpExpiredException(
            InvalidOtpException e) {
        return buildErrorResponse(
                "INVALID_OTP",
                e.getMessage(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(InvalidResetTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidResetTokenException(
            InvalidResetTokenException e) {
        return buildErrorResponse(
                "INVALID_RESET_TOKEN",
                e.getMessage(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(VerificationTokenExpiredException.class)
    public ResponseEntity<ErrorResponse> handleVerificationTokenExpiredException(
            VerificationTokenExpiredException e) {

        return buildErrorResponse(
                "VERIFICATION_TOKEN_EXPIRED",
                e.getMessage(),
                HttpStatus.GONE
        );
    }

    @ExceptionHandler(EmailAlreadyVerifiedException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyVerifiedException(
            EmailAlreadyVerifiedException e) {

        return buildErrorResponse(
                "EMAIL_ALREADY_VERIFIED",
                e.getMessage(),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(ChannelHandleAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleChannelHandleAlreadyExists(
            ChannelHandleAlreadyExistsException e) {
        return buildErrorResponse(
                "CHANNEL_HANDLE_ALREADY_EXISTS",
                e.getMessage(),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(InvalidChannelHandleException.class)
    public ResponseEntity<ErrorResponse> handleInvalidChannelHandle(
            InvalidChannelHandleException e) {
        return buildErrorResponse(
                "INVALID_CHANNEL_HANDLE",
                e.getMessage(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ChannelAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleChannelAccessDeniedException(ChannelAccessDeniedException e) {
        return buildErrorResponse(
                "CHANNEL_ACCESS_DENIED",
                e.getMessage(),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(ChannelNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleChannelNotFoundException(ChannelNotFoundException e) {
        return buildErrorResponse(
                "CHANNEL_NOT_FOUND",
                e.getMessage(),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(InvalidCursorException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCursorException(InvalidCursorException e) {
        return buildErrorResponse(
                "INVALID_CURSOR",
                e.getMessage(),
                HttpStatus.BAD_REQUEST
        );
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(String errorCode, String message, HttpStatus status) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(errorCode);
        errorResponse.setMessage(message);
        return ResponseEntity.status(status).body(errorResponse);
    }
}
