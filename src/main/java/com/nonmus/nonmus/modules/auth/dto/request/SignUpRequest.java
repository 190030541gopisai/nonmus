package com.nonmus.nonmus.modules.auth.dto.request;

import lombok.Data;

@Data
public class SignUpRequest {
    private String name;
    private String email;
    private String password;
}
