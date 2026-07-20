package com.nonmus.nonmus.security;

import com.nonmus.nonmus.modules.user.enums.Provider;
import lombok.Data;

@Data
public class AuthenticatedUser {
    private String email;
}
