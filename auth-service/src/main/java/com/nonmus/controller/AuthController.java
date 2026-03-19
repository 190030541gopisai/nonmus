package com.nonmus.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nonmus.dto.ApiResponse;
import com.nonmus.dto.EmailOtpVerifyRequest;
import com.nonmus.dto.RegisterRequest;
import com.nonmus.dto.RegisterResponse;
import com.nonmus.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("${api.prefix}")
@Validated
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        ApiResponse<RegisterResponse> response = authService.register(request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<?>> verifyOtp(@Valid @RequestBody EmailOtpVerifyRequest request) {
        ApiResponse<?> response = authService.verifyOtp(request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    
}
