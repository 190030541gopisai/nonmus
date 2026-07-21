package com.nonmus.nonmus.modules.auth.service;

import com.nonmus.nonmus.modules.auth.dto.internal.TokenPair;
import com.nonmus.nonmus.modules.common.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;

@Service
public class JwtCookieService {
    private final JwtUtil jwtUtil;

    public JwtCookieService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public void addJwtCookies(TokenPair tokenPair, HttpServletResponse response) {
        String accessToken = tokenPair.getAccessToken();
        if(StringUtils.hasText(accessToken)){
            addAccessTokenCookieToResponse(accessToken, response);
        }

        String refreshToken = tokenPair.getRefreshToken();
        if(StringUtils.hasText(refreshToken)) {
            addRefreshTokenCookieToResponse(refreshToken, response);
        }
    }

    public void removeJwtCookies(HttpServletResponse response) {
        removeAccessTokenCookieFromResponse(response);
        removeRefreshTokenCookieFromResponse(response);
    }

    public void addAccessTokenCookieToResponse(String accessToken, HttpServletResponse response) {
        ResponseCookie accessCookie = builder(
                        "access_token",
                        accessToken)
                .maxAge(jwtUtil.getRemainingDurationMs(accessToken))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
    }

    private void removeAccessTokenCookieFromResponse(HttpServletResponse response) {
        ResponseCookie accessCookie = builder(
                        "access_token",
                        "")
                .maxAge(Duration.ZERO)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
    }

    private void addRefreshTokenCookieToResponse(String refreshToken, HttpServletResponse response) {
        ResponseCookie refreshCookie = builder(
                        "refresh_token",
                        refreshToken)
                .maxAge(
                        jwtUtil.getRemainingDurationMs(refreshToken)
                )
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    private void removeRefreshTokenCookieFromResponse(HttpServletResponse response) {
        ResponseCookie refreshCookie = builder(
                        "refresh_token",
                        "")
                .maxAge(Duration.ZERO)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    private ResponseCookie.ResponseCookieBuilder builder(String name, String value) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/");
    }
}
