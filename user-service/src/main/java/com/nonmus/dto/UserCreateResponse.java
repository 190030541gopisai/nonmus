package com.nonmus.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class UserCreateResponse {
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private boolean emailVerified;
}
