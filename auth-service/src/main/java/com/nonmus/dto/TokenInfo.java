package com.nonmus.dto;

import lombok.Data;

@Data
public class TokenInfo {
    private String accessToken;
    private String refreshToken;
    private long expiresIn;
}
