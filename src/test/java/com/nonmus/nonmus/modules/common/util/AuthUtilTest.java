package com.nonmus.nonmus.modules.common.util;

import com.nonmus.nonmus.security.AuthenticatedUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class AuthUtilTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getPrincipal_shouldReturnAuthenticatedUser() {
        AuthenticatedUser user = new AuthenticatedUser();
        user.setEmail("user@test.com");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList()));

        AuthenticatedUser result = AuthUtil.getPrincipal();

        assertEquals("user@test.com", result.getEmail());
        assertSame(user, result);
    }
}