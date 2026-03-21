package com.nonmus.dto;

import java.time.Instant;

import lombok.Data;

@Data
public class Meta {
    private Instant timeStamp;
    private String requestId;
}
