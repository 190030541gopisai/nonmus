package com.nonmus.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class UserData {
    private UUID userId;
    private String email;
    private String firstName;
    private String lastName;
    private boolean emailVerified;
}
