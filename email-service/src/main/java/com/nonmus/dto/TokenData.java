package com.nonmus.dto;

import lombok.Data;

@Data
public class TokenData {
    private String accessToken;
    private String refreshToken;
    private long accessTokenExpiryInSeconds;
    private long refreshTokenExpiryInSeconds;
}
