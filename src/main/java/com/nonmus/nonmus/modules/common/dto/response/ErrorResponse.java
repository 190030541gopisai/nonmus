package com.nonmus.nonmus.modules.common.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class ErrorResponse {
    private String errorCode;
    private String message;
    private List<ErrorResponse> details;
}
