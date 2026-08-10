package com.nonmus.nonmus.modules.common.exception;

import com.nonmus.nonmus.modules.common.dto.response.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleException_shouldReturnInternalServerError() {
        ResponseEntity<ErrorResponse> response = handler.handleException(new RuntimeException());

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().getErrorCode());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
    }

    @Test
    void handleBadCredentialsException_shouldReturnUnauthorized() {
        ResponseEntity<ErrorResponse> response = handler.handleBadCredentialsException(
                new BadCredentialsException("bad creds"));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("INVALID_CREDENTIALS", response.getBody().getErrorCode());
        assertEquals("Invalid email or password.", response.getBody().getMessage());
    }

    @Test
    void handleUserAlreadyExistsException_shouldReturnConflict() {
        ResponseEntity<ErrorResponse> response = handler.handleUserAlreadyExistsException(
                new UserAlreadyExistsException("User exists"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("USER_ALREADY_EXISTS", response.getBody().getErrorCode());
        assertEquals("User exists", response.getBody().getMessage());
    }

    @Test
    void handleUserNotFoundException_shouldReturnNotFound() {
        ResponseEntity<ErrorResponse> response = handler.handleUserNotFoundException(
                new UserNotFoundException("User not found"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("USER_NOT_FOUND", response.getBody().getErrorCode());
        assertEquals("User not found", response.getBody().getMessage());
    }

    @Test
    void handleInvalidFileException_shouldReturnBadRequest() {
        ResponseEntity<ErrorResponse> response = handler.handleInvalidFileException(
                new InvalidFileException("Invalid file"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("INVALID_FILE", response.getBody().getErrorCode());
        assertEquals("Invalid file", response.getBody().getMessage());
    }

    @Test
    void handleStorageException_shouldReturnBadGateway() {
        ResponseEntity<ErrorResponse> response = handler.handleStorageException(
                new StorageException("Storage failed", new RuntimeException()));

        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
        assertEquals("STORAGE_ERROR", response.getBody().getErrorCode());
        assertEquals("File storage operation failed. Please try again.", response.getBody().getMessage());
    }

    @Test
    void handleInvalidVerificationTokenException_shouldReturnBadRequest() {
        ResponseEntity<ErrorResponse> response = handler.handleInvalidVerificationTokenException(
                new InvalidVerificationTokenException("Invalid token"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("INVALID_VERIFICATION_TOKEN", response.getBody().getErrorCode());
        assertEquals("Invalid token", response.getBody().getMessage());
    }

    @Test
    void handleOtpExpiredException_shouldReturnBadRequest() {
        ResponseEntity<ErrorResponse> response = handler.handleOtpExpiredException(
                new OtpExpiredException("OTP expired"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("OTP_EXPIRED", response.getBody().getErrorCode());
        assertEquals("OTP expired", response.getBody().getMessage());
    }

    @Test
    void handleInvalidOtpException_shouldReturnBadRequest() {
        ResponseEntity<ErrorResponse> response = handler.handleOtpExpiredException(
                new InvalidOtpException("Invalid OTP"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("INVALID_OTP", response.getBody().getErrorCode());
        assertEquals("Invalid OTP", response.getBody().getMessage());
    }

    @Test
    void handleInvalidResetTokenException_shouldReturnBadRequest() {
        ResponseEntity<ErrorResponse> response = handler.handleInvalidResetTokenException(
                new InvalidResetTokenException("Invalid reset token"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("INVALID_RESET_TOKEN", response.getBody().getErrorCode());
        assertEquals("Invalid reset token", response.getBody().getMessage());
    }

    @Test
    void handleVerificationTokenExpiredException_shouldReturnGone() {
        ResponseEntity<ErrorResponse> response = handler.handleVerificationTokenExpiredException(
                new VerificationTokenExpiredException("Token expired"));

        assertEquals(HttpStatus.GONE, response.getStatusCode());
        assertEquals("VERIFICATION_TOKEN_EXPIRED", response.getBody().getErrorCode());
        assertEquals("Token expired", response.getBody().getMessage());
    }

    @Test
    void handleEmailAlreadyVerifiedException_shouldReturnConflict() {
        ResponseEntity<ErrorResponse> response = handler.handleEmailAlreadyVerifiedException(
                new EmailAlreadyVerifiedException("Already verified"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("EMAIL_ALREADY_VERIFIED", response.getBody().getErrorCode());
        assertEquals("Already verified", response.getBody().getMessage());
    }
}