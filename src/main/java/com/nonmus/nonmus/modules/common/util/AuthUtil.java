package com.nonmus.nonmus.modules.common.util;

import com.nonmus.nonmus.modules.user.entity.Users;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class AuthUtil {
    private final JwtUtil jwtUtil;

    public void addJwtTokenCookiesToResponse(Users user, HttpServletResponse response) {
        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getName());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        addJwtTokenCookiesToResponse(accessToken, refreshToken, response);
    }

    public void removeJwtTokenCookiesFromResponse(HttpServletResponse response) {
        removeAccessTokenCookieFromResponse(response);
        removeRefreshTokenCookieFromResponse(response);
    }

    public void addAccessTokenCookieToResponse(Users user, HttpServletResponse response) {
        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getName());
        addAccessTokenCookieToResponse(accessToken, response);
    }

    private void addAccessTokenCookieToResponse(String accessToken, HttpServletResponse response) {
        ResponseCookie accessCookie = ResponseCookie.from(
                        "access_token",
                        accessToken)
                .httpOnly(true)
                .secure(true)              // false for local HTTP development
                .path("/")
                .sameSite("Lax")           // or "None" if cross-site
                .maxAge(Duration.ofMinutes(15))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
    }

    private void removeAccessTokenCookieFromResponse(HttpServletResponse response) {
        ResponseCookie accessCookie = ResponseCookie.from(
                        "access_token",
                        "")
                .httpOnly(true)
                .secure(true)              // false for local HTTP development
                .path("/")
                .sameSite("Lax")           // or "None" if cross-site
                .maxAge(Duration.ZERO)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
    }

    private void removeRefreshTokenCookieFromResponse(HttpServletResponse response) {
        ResponseCookie refreshCookie = ResponseCookie.from(
                        "refresh_token",
                        "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Lax")
                .maxAge(Duration.ZERO)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    private void addRefreshTokenCookieToResponse(String refreshToken, HttpServletResponse response) {
        ResponseCookie refreshCookie = ResponseCookie.from(
                        "refresh_token",
                        refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Lax")
                .maxAge(Duration.ofDays(7))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    private void addJwtTokenCookiesToResponse(String accessToken, String refreshToken, HttpServletResponse response) {
        addAccessTokenCookieToResponse(accessToken, response);
        addRefreshTokenCookieToResponse(refreshToken, response);
    }

    public static String getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (String) authentication.getPrincipal();
    }
}
