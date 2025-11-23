package com.nonmus.auth_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendEmailOtpResponse {
    private String status;
    private String message;
}
