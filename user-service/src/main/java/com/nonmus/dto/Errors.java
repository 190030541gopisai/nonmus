package com.nonmus.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
public class Errors {
    private String code;
    private String message;
    private List<ErrorDetail> details;
}
