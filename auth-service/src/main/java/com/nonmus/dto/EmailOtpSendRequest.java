package com.nonmus.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class EmailOtpSendRequest {
    private UUID userId;
    private String email;
}
