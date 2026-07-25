package com.nonmus.nonmus.modules.common.util;

import com.nonmus.nonmus.modules.common.util.props.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        JwtProperties props = new JwtProperties();
        props.setSecretKey("my-super-secret-key-that-is-at-least-256-bits-long-for-hs256!");
        jwtUtil = new JwtUtil(props);
    }

    @Test
    void generateAccessToken_shouldCreateValidToken() {
        String token = jwtUtil.generateAccessToken("user@test.com", "Test User", 3600000);

        assertNotNull(token);
        assertEquals("user@test.com", jwtUtil.getEmailFromToken(token));
        assertTrue(jwtUtil.isTokenValid(token));
    }

    @Test
    void generateRefreshToken_shouldCreateValidToken() {
        String token = jwtUtil.generateRefreshToken("user@test.com", 86400000);

        assertNotNull(token);
        assertEquals("user@test.com", jwtUtil.getEmailFromToken(token));
        assertTrue(jwtUtil.isTokenValid(token));
    }

    @Test
    void isTokenValid_shouldReturnFalse_forExpiredToken() {
        String token = jwtUtil.generateAccessToken("user@test.com", "Test", -1);

        assertFalse(jwtUtil.isTokenValid(token));
    }

    @Test
    void isTokenValid_shouldReturnFalse_forMalformedToken() {
        assertFalse(jwtUtil.isTokenValid("not-a-jwt-token"));
    }

    @Test
    void getRemainingDurationMs_shouldReturnPositiveDuration() {
        String token = jwtUtil.generateAccessToken("user@test.com", "Test", 3600000);

        Duration duration = jwtUtil.getRemainingDurationMs(token);

        assertTrue(duration.toMillis() > 0);
        assertTrue(duration.toMillis() <= 3600000);
    }

    @Test
    void getRemainingDurationMs_shouldThrow_forExpiredToken() {
        String token = jwtUtil.generateAccessToken("user@test.com", "Test", -1);

        assertThrows(io.jsonwebtoken.ExpiredJwtException.class,
                () -> jwtUtil.getRemainingDurationMs(token));
    }

    @Test
    void getExpiration_shouldReturnDateInFuture() {
        String token = jwtUtil.generateAccessToken("user@test.com", "Test", 3600000);

        assertTrue(jwtUtil.getExpiration(token).getTime() > System.currentTimeMillis());
    }
}