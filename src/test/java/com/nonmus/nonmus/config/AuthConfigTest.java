package com.nonmus.nonmus.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class AuthConfigTest {
    @Test
    void passwordEncoder_ShouldReturnBcryptPasswordEncoder() {
        AuthConfig authConfig = new AuthConfig();

        PasswordEncoder passwordEncoder = authConfig.passwordEncoder();

        assertNotNull(passwordEncoder);
        assertInstanceOf(BCryptPasswordEncoder.class, passwordEncoder);

        String rawPassword = "password123";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
    }
}