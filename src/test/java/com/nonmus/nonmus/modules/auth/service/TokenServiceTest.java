package com.nonmus.nonmus.modules.auth.service;

import com.nonmus.nonmus.modules.auth.dto.internal.TokenPair;
import com.nonmus.nonmus.modules.common.util.JwtUtil;
import com.nonmus.nonmus.modules.common.util.props.JwtProperties;
import com.nonmus.nonmus.modules.user.entity.Users;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private TokenService tokenService;

    @Test
    void generateJwtTokens_shouldReturnTokenPairWithRememberMeExpiry_whenRememberMeIsTrue() {
        Users user = new Users();
        user.setEmail("test@test.com");
        user.setName("Test");

        when(jwtProperties.getJwtExpirationMs()).thenReturn(3600000L);
        when(jwtProperties.getRememberMeRefreshTokenExpirationMs()).thenReturn(2592000000L);
        when(jwtUtil.generateAccessToken("test@test.com", "Test", 3600000L)).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken("test@test.com", 2592000000L)).thenReturn("refresh-token");

        TokenPair result = tokenService.generateJwtTokens(user, true);

        assertEquals("access-token", result.getAccessToken());
        assertEquals("refresh-token", result.getRefreshToken());
    }

    @Test
    void generateJwtTokens_shouldReturnTokenPairWithDefaultExpiry_whenRememberMeIsFalse() {
        Users user = new Users();
        user.setEmail("test@test.com");
        user.setName("Test");

        when(jwtProperties.getJwtExpirationMs()).thenReturn(3600000L);
        when(jwtProperties.getRefreshTokenExpirationMs()).thenReturn(86400000L);
        when(jwtUtil.generateAccessToken("test@test.com", "Test", 3600000L)).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken("test@test.com", 86400000L)).thenReturn("refresh-token");

        TokenPair result = tokenService.generateJwtTokens(user, false);

        assertEquals("access-token", result.getAccessToken());
        assertEquals("refresh-token", result.getRefreshToken());
    }

    @Test
    void generateJwtTokens_withSingleArg_shouldDelegateWithRememberMeFalse() {
        Users user = new Users();
        user.setEmail("test@test.com");
        user.setName("Test");

        when(jwtProperties.getJwtExpirationMs()).thenReturn(3600000L);
        when(jwtProperties.getRefreshTokenExpirationMs()).thenReturn(86400000L);
        when(jwtUtil.generateAccessToken("test@test.com", "Test", 3600000L)).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken("test@test.com", 86400000L)).thenReturn("refresh-token");

        TokenPair result = tokenService.generateJwtTokens(user);

        assertEquals("access-token", result.getAccessToken());
        assertEquals("refresh-token", result.getRefreshToken());
        verify(jwtUtil).generateRefreshToken("test@test.com", jwtProperties.getRefreshTokenExpirationMs());
    }

    @Test
    void generateAccessToken_shouldReturnAccessToken() {
        Users user = new Users();
        user.setEmail("test@test.com");
        user.setName("Test");

        when(jwtProperties.getJwtExpirationMs()).thenReturn(3600000L);
        when(jwtUtil.generateAccessToken("test@test.com", "Test", 3600000L)).thenReturn("access-token");

        String token = tokenService.generateAccessToken(user);

        assertEquals("access-token", token);
    }
}