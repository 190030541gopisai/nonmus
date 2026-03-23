package com.nonmus.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private UserData user;
    private TokenInfo tokenInfo;
}
