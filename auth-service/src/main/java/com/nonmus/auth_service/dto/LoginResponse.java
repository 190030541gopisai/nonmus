package com.nonmus.auth_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private Status status;
    private String token;
    private String refreshToken;
    private String message;
}
