package com.nonmus.nonmus.modules.auth.service;

import com.nonmus.nonmus.modules.auth.dto.internal.LoginResult;
import com.nonmus.nonmus.modules.auth.dto.request.ForgotPasswordRequest;
import com.nonmus.nonmus.modules.auth.dto.response.OAuth2Response;
import com.nonmus.nonmus.modules.auth.events.EmailForgotPasswordEvent;
import com.nonmus.nonmus.modules.common.exception.UserNotFoundException;
import com.nonmus.nonmus.modules.common.util.AuthUtil;
import com.nonmus.nonmus.modules.user.dto.request.OAuthUserCreateRequest;
import com.nonmus.nonmus.modules.user.enums.Provider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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
    private final UsersService usersService;
    private final PasswordEncoder passwordEncoder;
    private final AuthUtil authUtil;
    private final ApplicationEventPublisher publisher;
    private final TokenService tokenService;
    private final JwtCookieService jwtCookieService;

    public LoginResult login(String email, String password, boolean rememberMe) {
        Users user = usersService.getUsersByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        String existingPassword = user.getPassword();
        if (!StringUtils.hasText(existingPassword) || !passwordEncoder.matches(password, existingPassword)) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return LoginResult.builder()
                .user(user)
                .tokenPair(
                        tokenService.generateJwtTokens(user, rememberMe)
                )
                .build();
    }

    public void logout(HttpServletResponse response) {
        jwtCookieService.removeJwtCookies(response);
    }

    public void forgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail();

        Users user = usersService.getUsersByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));

        EmailForgotPasswordEvent event = new EmailForgotPasswordEvent(user);
        publisher.publishEvent(event);
    }
}
