package com.nonmus.auth_service.controller;

import com.nonmus.auth_service.config.jwt.JwtService;
import com.nonmus.auth_service.constants.Constants;
import com.nonmus.auth_service.dto.*;
import com.nonmus.auth_service.service.EmailOtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/auth/")
@RequiredArgsConstructor
public class EmailOtpController {

    private final EmailOtpService emailOtpService;
    private final JwtService jwtService;

    @PostMapping("/send-email-otp")
    public ResponseEntity<SendEmailOtpResponse> sendEmailOtp(@RequestBody SendEmailOtpRequest request) {
        emailOtpService.sendEmailOtp(request);

        SendEmailOtpResponse response = new SendEmailOtpResponse();
        response.setStatus(Constants.SUCCESS);

        response.setMessage("If an account with this email exists and requires verification, an OTP has been sent.");

        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @PostMapping("/verify-email-otp")
    public ResponseEntity<LoginResponse> verifyEmailOtp(@Valid @RequestBody VerifyEmailOtpRequest request) {
        boolean isOtpVerified = emailOtpService.verifyEmailOtp(request);

        LoginResponse response = new LoginResponse();

        if (isOtpVerified) {
            response.setStatus("OTP_VERIFICATION_SUCCESS");
            response.setMessage("Email Otp Verification Successful");

            String accessToken = jwtService.generateToken(request.getEmail());
            String refreshToken = jwtService.generateRefreshToken(request.getEmail());

            response.setToken(accessToken);
            response.setRefreshToken(refreshToken);

            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        response.setStatus("OTP_VERIFICATION_FAILED");
        response.setMessage("Email Otp Verification failed");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
