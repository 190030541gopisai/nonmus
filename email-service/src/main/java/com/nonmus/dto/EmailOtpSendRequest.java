package com.nonmus.dto;

import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class EmailOtpSendRequest {
    @NotNull(message = "User Id is required")
    private UUID userId;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid Email Format")
    private String email;
}
