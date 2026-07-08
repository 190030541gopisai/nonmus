package com.nonmus.nonmus.modules.auth.util;

import com.nonmus.nonmus.modules.auth.dto.response.AuthResponse;
import com.nonmus.nonmus.modules.common.util.JwtUtil;
import com.nonmus.nonmus.modules.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthResponseGenerator {
    private final JwtUtil jwtUtil;

    public AuthResponse generateAuthResponse(Users user, String message) {
        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getName());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        return new AuthResponse(
                accessToken,
                refreshToken,
                message,
                user.getEmail(),
                user.getName()
        );
    }
}
