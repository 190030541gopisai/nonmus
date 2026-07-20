package com.nonmus.nonmus.modules.auth.dto.request;

import lombok.Data;

@Data
public class ResendVerificationEmailRequest {
    private String email;
}
