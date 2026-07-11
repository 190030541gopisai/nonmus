package com.nonmus.nonmus.modules.auth.controller;

import com.nonmus.nonmus.modules.auth.dto.request.LoginRequest;
import com.nonmus.nonmus.modules.auth.dto.response.LoginResponse;
import com.nonmus.nonmus.modules.auth.dto.response.SignUpResponse;
import com.nonmus.nonmus.modules.auth.service.AuthenticationService;
import com.nonmus.nonmus.modules.auth.service.UserRegistrationService;
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

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signup(@RequestBody SignUpRequest request, HttpServletResponse response) {
        SignUpResponse signUpResponse = userRegistrationService.signup(request, response);
        return ResponseEntity.status(HttpStatus.CREATED).body(signUpResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        LoginResponse loginResponse = authService.login(request.getEmail(), request.getPassword(), response);
        return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
    }

    @GetMapping("/refresh")
    public void refresh(HttpServletRequest request, HttpServletResponse response) {
        authService.refreshAccessToken(request, response);
    }
}