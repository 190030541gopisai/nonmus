package com.nonmus.nonmus.modules.auth.controller;

import com.nonmus.nonmus.modules.auth.dto.request.LoginRequest;
import com.nonmus.nonmus.modules.auth.service.AuthenticationService;
import com.nonmus.nonmus.modules.auth.service.UserRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nonmus.nonmus.modules.auth.dto.request.SignUpRequest;
import com.nonmus.nonmus.modules.auth.dto.response.AuthResponse;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authService;
    private final UserRegistrationService userRegistrationService;

    @PostMapping("/signup")
    public AuthResponse signup(@RequestBody SignUpRequest request) {
        return userRegistrationService.signup(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request.getEmail(), request.getPassword());
    }
}