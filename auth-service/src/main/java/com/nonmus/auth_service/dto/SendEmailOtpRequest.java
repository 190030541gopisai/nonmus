package com.nonmus.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendEmailOtpRequest {
    @NotBlank(message = "Email is Required")
    @Email(message = "Invalid email format")
    private String email;
}
