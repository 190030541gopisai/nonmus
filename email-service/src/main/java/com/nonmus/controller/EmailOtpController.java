package com.nonmus.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nonmus.dto.ApiResponse;
import com.nonmus.dto.EmailOtpSendRequest;
import com.nonmus.dto.EmailOtpSendResponse;
import com.nonmus.factory.EmailOtpSendResponseFactory;
import com.nonmus.service.EmailOtpService;
import com.nonmus.service.OtpCacheService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("${api.prefix}/otp")
@Validated
public class EmailOtpController {
    
    private final EmailOtpService emailOtpService;
    private final OtpCacheService otpCacheService;

    public EmailOtpController(EmailOtpService emailOtpService, OtpCacheService otpCacheService) {
        this.emailOtpService = emailOtpService;
        this.otpCacheService = otpCacheService;
    }

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<?>> sendOtp(@Valid @RequestBody EmailOtpSendRequest request) throws Exception {
        EmailOtpSendResponse otpSendResponse = emailOtpService.sendOtp(request);

        ApiResponse<?> response = EmailOtpSendResponseFactory.getApiResponse(otpSendResponse.getCode(), request.getUserId(), otpCacheService);
        
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
