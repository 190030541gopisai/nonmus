package com.nonmus.nonmus.modules.auth.service;

import com.nonmus.nonmus.modules.auth.dto.response.LoginResponse;
import com.nonmus.nonmus.modules.auth.dto.response.OAuth2Response;
import com.nonmus.nonmus.modules.common.exception.UserNotFoundException;
import com.nonmus.nonmus.modules.common.util.AuthUtil;
import com.nonmus.nonmus.modules.common.util.JwtUtil;
import com.nonmus.nonmus.modules.user.dto.request.OAuthUserCreateRequest;
import com.nonmus.nonmus.modules.user.enums.Provider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.nonmus.nonmus.modules.user.entity.Users;
import com.nonmus.nonmus.modules.user.service.UsersService;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final String REFRESH_TOKEN_COOKIE = "refresh_token";

    private final UsersService usersService;
    private final PasswordEncoder passwordEncoder;
    private final AuthUtil authUtil;
    private final JwtUtil jwtUtil;

    public LoginResponse login(String email, String password, boolean rememberMe, HttpServletResponse response) {
        Provider provider = Provider.LOCAL;
        Users user = usersService.getUsersByEmailAndProvider(email, provider)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        authUtil.addJwtTokenCookiesToResponse(user, provider, rememberMe, response);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setMessage("Login Successfull");

        return loginResponse;
    }

    public OAuth2Response oauthSingIn(OAuth2User oauthUser, Provider provider, HttpServletResponse response){
        String email = oauthUser.getAttribute("email");
        boolean rememberMe = true;

        if(usersService.existsByEmailAndProvider(email, provider)) {
            Users user = usersService.getUsersByEmailAndProvider(email, provider).get();
            authUtil.addJwtTokenCookiesToResponse(user, provider, rememberMe, response);

            OAuth2Response oAuth2Response = new OAuth2Response();
            oAuth2Response.setMessage("Authentication successful");

            return oAuth2Response;
        }

        OAuthUserCreateRequest request = new OAuthUserCreateRequest();
        request.setName(oauthUser.getAttribute("name"));
        request.setEmail(oauthUser.getAttribute("email"));
        request.setExternalProfilePictureUrl(oauthUser.getAttribute("picture"));
        request.setProvider(provider);

        Users user = usersService.createOAuthUser(request);
        authUtil.addJwtTokenCookiesToResponse(user, provider, rememberMe, response);

        OAuth2Response oAuth2Response = new OAuth2Response();
        oAuth2Response.setMessage("Authentication successful");

        return oAuth2Response;
    }

    public void refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshToken(request);

        if(StringUtils.hasText(refreshToken) && jwtUtil.isTokenValid(refreshToken)) {
            String email = jwtUtil.getEmailFromToken(refreshToken);
            Provider provider = jwtUtil.getProviderFromToken(refreshToken);
            Users user = usersService.getUsersByEmailAndProvider(email, provider).orElseThrow(() -> new UserNotFoundException("User not found"));
            authUtil.addAccessTokenCookieToResponse(user, provider, response);
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

    public void logout(HttpServletResponse response) {
        authUtil.removeJwtTokenCookiesFromResponse(response);
    }
}
