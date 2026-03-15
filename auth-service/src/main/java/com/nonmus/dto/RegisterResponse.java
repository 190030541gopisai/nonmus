package com.nonmus.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class RegisterResponse {
    private UUID userId;
    private String email;
    private String firstName;
    private String lastName;
    private boolean emailVerified;
    private boolean otpSent;
    private String otpStatusCode;
    private String otpMessage;
}
