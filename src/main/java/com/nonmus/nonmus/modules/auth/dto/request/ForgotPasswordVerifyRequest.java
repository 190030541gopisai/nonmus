package com.nonmus.nonmus.modules.auth.dto.request;

import lombok.Data;

@Data
public class ForgotPasswordVerifyRequest {
    private String email;
    private String code;
}
