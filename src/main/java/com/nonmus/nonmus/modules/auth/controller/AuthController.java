package com.nonmus.nonmus.modules.auth.controller;

import com.nonmus.nonmus.modules.auth.dto.internal.LoginResult;
import com.nonmus.nonmus.modules.auth.dto.internal.SignUpResult;
import com.nonmus.nonmus.modules.auth.dto.request.LoginRequest;
import com.nonmus.nonmus.modules.auth.dto.response.LoginResponse;
import com.nonmus.nonmus.modules.auth.dto.response.SignUpResponse;
import com.nonmus.nonmus.modules.auth.service.AuthenticationService;
import com.nonmus.nonmus.modules.auth.service.JwtCookieService;
import com.nonmus.nonmus.modules.auth.service.RefreshTokenService;
import com.nonmus.nonmus.modules.auth.service.UserRegistrationService;
import com.nonmus.nonmus.modules.common.util.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nonmus.nonmus.modules.auth.dto.request.SignUpRequest;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authService;
    private final UserRegistrationService userRegistrationService;
    private final RefreshTokenService refreshTokenService;
    private final AuthUtil authUtil;
    private final JwtCookieService jwtCookieService;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signup(@RequestBody SignUpRequest request, HttpServletResponse response) {
        SignUpResult signUpResult = userRegistrationService.signup(request);

        jwtCookieService.addJwtCookies(signUpResult.getTokenPair(), response);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        SignUpResponse.builder()
                                .message("Signup successful")
                                .build()
                );
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        LoginResult loginResult = authService.login(request.getEmail(), request.getPassword(), request.isRememberMe());

        jwtCookieService.addJwtCookies(loginResult.getTokenPair(), response);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                    LoginResponse
                            .builder()
                            .message("Login Successful")
                            .build()
                );
    }

    @PostMapping("/refresh")
    public void refresh(HttpServletRequest request, HttpServletResponse response) {
        refreshTokenService.refreshAccessToken(request, response);
    }

    @PostMapping("/logout")
    public void logout(HttpServletResponse response) {
        authService.logout(response);
    }
}