package com.nonmus.nonmus.modules.common.util;

import com.nonmus.nonmus.modules.common.util.props.JwtProperties;
import com.nonmus.nonmus.modules.user.enums.Provider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final JwtProperties jwtProperties;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes());
    }

    public String generateAccessToken(String email, String name, Provider provider) {
        return Jwts.builder()
                .subject(email)
                .claim("name", name)
                .claim("provider", provider.name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getJwtExpirationMs()))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String email, Provider provider, boolean rememberMe) {
        return Jwts.builder()
                .subject(email)
                .claim("provider", provider.name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + (rememberMe ?
                        jwtProperties.getRememberMeRefreshTokenExpirationMs():
                        jwtProperties.getRefreshTokenExpirationMs()
                )))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public Provider getProviderFromToken(String token) {
        String providerName = (String) Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("provider");

        return Provider.valueOf(providerName);
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
