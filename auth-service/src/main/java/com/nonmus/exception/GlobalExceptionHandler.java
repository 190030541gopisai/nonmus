package com.nonmus.exception;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nonmus.constants.AppConstants;
import com.nonmus.dto.ApiResponse;
import com.nonmus.dto.Empty;
import com.nonmus.dto.ErrorDetail;
import com.nonmus.dto.Errors;
import com.nonmus.dto.Meta;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationExceptions(MethodArgumentNotValidException exception) {
        ApiResponse<Empty> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setStatusCode(422);
        response.setMessage("Validation Failed");
        response.setData(null);

        Meta meta = new Meta();
        meta.setTimeStamp(Instant.now());

        response.setMeta(meta);

        Errors errors = new Errors();
        errors.setCode("VALIDATION_ERROR");
        errors.setMessage("One or more fields are invalid");
        
        List<ErrorDetail> errorDetails = new ArrayList<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->{
            ErrorDetail errorDetail = new ErrorDetail();
            errorDetail.setField(error.getField());
            errorDetail.setMessage(error.getDefaultMessage());

            errorDetails.add(errorDetail);
        });

        errors.setDetails(errorDetails);

        response.setErrors(errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(value = UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<?>> userAlreadyExistsException(UserAlreadyExistsException exception) {
        ApiResponse<Empty> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setStatusCode(409);
        response.setMessage("User Conflict");
        response.setData(null);

        Meta meta = new Meta();
        meta.setTimeStamp(Instant.now());

        response.setMeta(meta);

        Errors errors = new Errors();
        errors.setCode("USER_ALREADY_EXISTS");
        errors.setMessage(exception.getMessage());

        response.setErrors(errors);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(UserServiceException.class)
    public ResponseEntity<ApiResponse<?>> userServiceException(UserServiceException exception) {
        ApiResponse<Empty> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setStatusCode(exception.getStatusCode());
        response.setMessage(exception.getMessage());
        response.setData(null);

        Meta meta = new Meta();
        meta.setTimeStamp(Instant.now());
        response.setMeta(meta);

        Errors errors = new Errors();
        errors.setCode(exception.getCode());
        errors.setMessage(exception.getMessage());
        response.setErrors(errors);

        return ResponseEntity.status(exception.getStatusCode()).body(response);
    }

    @ExceptionHandler(EmailServiceException.class)
    public ResponseEntity<?> handleEmailServiceException(EmailServiceException ex) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            Object jsonBody = mapper.readValue(ex.getResponseBody(), Object.class);

            return ResponseEntity
                    .status(ex.getStatus())
                    .body(jsonBody);

        } catch (Exception e) {

            return ResponseEntity
                    .status(ex.getStatus())
                    .body(ex.getResponseBody());
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> defaultExceptionHandler(Exception exception) {
        ApiResponse<Empty> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setStatusCode(500);
        response.setMessage("Internal server error");
        response.setData(null);

        Meta meta = new Meta();
        meta.setTimeStamp(Instant.now());
        response.setMeta(meta);

        Errors errors = new Errors();
        errors.setCode(AppConstants.INTERNAL_SERVER_ERROR);
        errors.setMessage(exception.getMessage());
        response.setErrors(errors);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
