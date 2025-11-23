package com.nonmus.auth_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;


@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String STATUS = "status";
    private static final String MESSAGE = "message";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();

            errors.put(fieldName, message);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<Map<String, String>>  handleEmailNotVerifiedException(EmailNotVerifiedException ex) {
        Map<String, String> response = new HashMap<>();
        response.put(STATUS, "EMAIL_NOT_VERIFIED");
        response.put(MESSAGE, ex.getMessage());

        // Use 403 Forbidden for a user that exists but lacks permissions (i.e., is not verified)
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentialsException(BadCredentialsException ex) {
        Map<String, String> response = new HashMap<>();
        response.put(STATUS, "BAD_CREDENTIALS");
        response.put(MESSAGE, ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<Map<String, String>> handleInvalidOtpException(InvalidOtpException ex, WebRequest request) {
        Map<String, String> response = new HashMap<>();
        response.put(STATUS, "INVALID_OTP");
        response.put(MESSAGE, ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<Map<String, String>> handleRateLimitException(RateLimitException ex, WebRequest request) {
        Map<String, String> response = new HashMap<>();
        response.put(STATUS, "RATE_LIMIT_EXCEEDED");
        response.put(MESSAGE, ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.TOO_MANY_REQUESTS);
    }


    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleUserAlreadyExistsException(UserAlreadyExistsException ex, WebRequest request) {
        Map<String, String> response = new HashMap<>();
        response.put(STATUS, "USER_ALREADY_EXISTS");
        response.put(MESSAGE, ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }


    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        Map<String, String> response = new HashMap<>();
        response.put(STATUS, "USER_NOT_FOUND");
        response.put(MESSAGE, ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGlobalException(Exception ex, WebRequest request) {
        Map<String, String> response = new HashMap<>();
        response.put(STATUS, "INTERNAL_SERVER_ERROR");
        response.put(MESSAGE, "An internal server error occurred. Please try again later.");

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
