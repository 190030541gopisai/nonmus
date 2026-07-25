package com.nonmus.nonmus.modules.auth.service;

import com.nonmus.nonmus.modules.auth.dto.internal.TokenPair;
import com.nonmus.nonmus.modules.common.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtCookieServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private JwtCookieService jwtCookieService;

    @Captor
    private ArgumentCaptor<String> headerCaptor;

    @Test
    void addJwtCookies_shouldAddBothCookies_whenBothTokensPresent() {
        TokenPair tokenPair = new TokenPair("access-value", "refresh-value");
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(jwtUtil.getRemainingDurationMs("access-value")).thenReturn(Duration.ofMinutes(15));
        when(jwtUtil.getRemainingDurationMs("refresh-value")).thenReturn(Duration.ofDays(7));

        jwtCookieService.addJwtCookies(tokenPair, response);

        verify(response, times(2)).addHeader(eq(HttpHeaders.SET_COOKIE), headerCaptor.capture());

        var cookies = headerCaptor.getAllValues();
        assertTrue(cookies.get(0).startsWith("access_token=access-value;"));
        assertTrue(cookies.get(0).contains("HttpOnly"));
        assertTrue(cookies.get(0).contains("Secure"));
        assertTrue(cookies.get(0).contains("Path=/"));
        assertTrue(cookies.get(1).startsWith("refresh_token=refresh-value;"));
        assertTrue(cookies.get(1).contains("HttpOnly"));
        assertTrue(cookies.get(1).contains("Secure"));
        assertTrue(cookies.get(1).contains("Path=/"));
    }

    @Test
    void addJwtCookies_shouldOnlyAddAccessCookie_whenAccessTokenPresent() {
        TokenPair tokenPair = new TokenPair("access-value", null);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(jwtUtil.getRemainingDurationMs("access-value")).thenReturn(Duration.ofMinutes(15));

        jwtCookieService.addJwtCookies(tokenPair, response);

        verify(response).addHeader(eq(HttpHeaders.SET_COOKIE), headerCaptor.capture());
        assertTrue(headerCaptor.getValue().startsWith("access_token=access-value;"));
    }

    @Test
    void addJwtCookies_shouldOnlyAddRefreshCookie_whenRefreshTokenPresent() {
        TokenPair tokenPair = new TokenPair(null, "refresh-value");
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(jwtUtil.getRemainingDurationMs("refresh-value")).thenReturn(Duration.ofDays(7));

        jwtCookieService.addJwtCookies(tokenPair, response);

        verify(response).addHeader(eq(HttpHeaders.SET_COOKIE), headerCaptor.capture());
        assertTrue(headerCaptor.getValue().startsWith("refresh_token=refresh-value;"));
    }

    @Test
    void addJwtCookies_shouldAddNoCookies_whenBothTokensEmpty() {
        TokenPair tokenPair = new TokenPair("", "");
        HttpServletResponse response = mock(HttpServletResponse.class);

        jwtCookieService.addJwtCookies(tokenPair, response);

        verify(response, never()).addHeader(any(), any());
    }

    @Test
    void removeJwtCookies_shouldAddBothCookiesWithMaxAgeZero() {
        HttpServletResponse response = mock(HttpServletResponse.class);

        jwtCookieService.removeJwtCookies(response);

        verify(response, times(2)).addHeader(eq(HttpHeaders.SET_COOKIE), headerCaptor.capture());

        var cookies = headerCaptor.getAllValues();
        assertTrue(cookies.get(0).startsWith("access_token=;"));
        assertTrue(cookies.get(0).contains("Max-Age=0"));
        assertTrue(cookies.get(1).startsWith("refresh_token=;"));
        assertTrue(cookies.get(1).contains("Max-Age=0"));
    }

    @Test
    void addAccessTokenCookieToResponse_shouldAddCookie() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(jwtUtil.getRemainingDurationMs("test-token")).thenReturn(Duration.ofMinutes(15));

        jwtCookieService.addAccessTokenCookieToResponse("test-token", response);

        verify(response).addHeader(eq(HttpHeaders.SET_COOKIE), headerCaptor.capture());
        assertTrue(headerCaptor.getValue().startsWith("access_token=test-token;"));
    }
}