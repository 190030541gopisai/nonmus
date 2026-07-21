package com.nonmus.nonmus.modules.auth.service;

import com.nonmus.nonmus.modules.auth.dto.internal.TokenPair;
import com.nonmus.nonmus.modules.common.exception.UserNotFoundException;
import com.nonmus.nonmus.modules.common.util.AuthUtil;
import com.nonmus.nonmus.modules.common.util.JwtUtil;
import com.nonmus.nonmus.modules.user.entity.Users;
import com.nonmus.nonmus.modules.user.service.UsersService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.Token;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final String REFRESH_TOKEN_COOKIE = "refresh_token";

    private final UsersService usersService;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;
    private final JwtCookieService jwtCookieService;

    public void refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshToken(request);

        if(StringUtils.hasText(refreshToken) && jwtUtil.isTokenValid(refreshToken)) {
            String email = jwtUtil.getEmailFromToken(refreshToken);
            Users user = usersService.getUsersByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));

            jwtCookieService.addAccessTokenCookieToResponse(
                    tokenService.generateAccessToken(user),
                    response
            );
        }
    }

    private String extractRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (REFRESH_TOKEN_COOKIE.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}
