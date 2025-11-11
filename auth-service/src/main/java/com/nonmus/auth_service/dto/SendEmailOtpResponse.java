package com.nonmus.auth_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendEmailOtpResponse {
    private Status status;
    private String message;
}
