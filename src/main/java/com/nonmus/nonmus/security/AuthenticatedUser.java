package com.nonmus.nonmus.security;

import lombok.Data;

@Data
public class AuthenticatedUser {
    private String email;
    private boolean emailVerified;
}
