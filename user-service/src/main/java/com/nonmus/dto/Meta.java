package com.nonmus.dto;

import java.time.Instant;

import lombok.Builder;
import lombok.Data;

@Data
public class Meta {
    private Instant timeStamp;
    private String requestId;
}
