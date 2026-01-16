package com.nonmus.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
public class UserCreateResponse {
    private UUID userId;
    private String email;
    private String firstName;
    private String lastName;
    private boolean emailVerified;
}
