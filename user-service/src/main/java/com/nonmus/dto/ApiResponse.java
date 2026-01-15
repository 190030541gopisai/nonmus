package com.nonmus.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class ApiResponse<T> {
    private boolean success;
    private int statusCode;
    private String message;
    private T data;
    private Meta meta;
    private Errors errors;
}
