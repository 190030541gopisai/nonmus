package com.nonmus.dto;

import lombok.Data;

@Data
public class EmailOtpVerifyResponse {
    private String code;
    private TokenData tokenData;
    private UserData userData;
}
