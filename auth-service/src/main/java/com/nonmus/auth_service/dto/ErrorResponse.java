package com.nonmus.auth_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // This will hide null fields (like nextAction)
public class ErrorResponse {
    private String status;
    private String message;
    private String nextAction; // Optional field

    public ErrorResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public ErrorResponse(String status, String message, String nextAction) {
        this.status = status;
        this.message = message;
        this.nextAction = nextAction;
    }
}
