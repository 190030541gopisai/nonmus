package com.nonmus.nonmus.modules.auth.service;

import com.nonmus.nonmus.modules.auth.dto.internal.TokenPair;
import com.nonmus.nonmus.modules.common.util.JwtUtil;
import com.nonmus.nonmus.modules.common.util.props.JwtProperties;
import com.nonmus.nonmus.modules.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;

    public TokenPair generateJwtTokens(Users user, boolean rememberMe) {
        return TokenPair.builder()
                .accessToken(
                        jwtUtil.generateAccessToken(
                                user.getEmail(),
                                user.getName(),
                                jwtProperties.getJwtExpirationMs()
                        )
                )
                .refreshToken(
                        jwtUtil.generateRefreshToken(
                                user.getEmail(),
                                rememberMe ?
                                        jwtProperties.getRememberMeRefreshTokenExpirationMs() :
                                        jwtProperties.getRefreshTokenExpirationMs()
                        )
                )
                .build();
    }

    public TokenPair generateJwtTokens(Users user) {
        return generateJwtTokens(user, false);
    }

    public String generateAccessToken(Users user) {
        return jwtUtil.generateAccessToken(
                user.getEmail(),
                user.getName(),
                jwtProperties.getJwtExpirationMs()
        );
    }
}
