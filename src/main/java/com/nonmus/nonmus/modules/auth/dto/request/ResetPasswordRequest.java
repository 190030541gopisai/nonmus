package com.nonmus.nonmus.modules.auth.dto.request;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String newPassword;
}
