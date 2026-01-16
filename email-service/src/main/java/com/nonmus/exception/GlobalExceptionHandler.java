package com.nonmus.exception;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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


    @ExceptionHandler(value = Exception.class)
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
        errors.setCode("INTERNAL_SERVER_ERROR");
        errors.setMessage(exception.getMessage());

        response.setErrors(errors);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
