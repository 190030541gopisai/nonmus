package com.nonmus.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class ErrorDetail {
    private String field;
    private String message;
}
