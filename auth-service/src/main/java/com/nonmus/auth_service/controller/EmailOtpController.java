package com.nonmus.auth_service.controller;


import com.nonmus.auth_service.dto.*;
import com.nonmus.auth_service.service.EmailOtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/")
@RequiredArgsConstructor
public class EmailOtpController {

    private final EmailOtpService emailOtpService;

    @PostMapping("/send-email-otp")
    public ResponseEntity<SendEmailOtpResponse> sendEmailOtp(@RequestBody SendEmailOtpRequest request) {
        emailOtpService.sendEmailOtp(request);

        SendEmailOtpResponse response = new SendEmailOtpResponse();
        response.setStatus(Status.SUCCESS);

        response.setMessage("If an account with this email exists and requires verification, an OTP has been sent.");

        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @PostMapping("/verify-email-otp")
    public ResponseEntity<LoginResponse> verifyEmailOtp(@RequestBody VerifyEmailOtpRequest request) {
        boolean isOtpVerified = emailOtpService.verifyEmailOtp(request);

        LoginResponse response = new LoginResponse();

        if(isOtpVerified) {
            response.setStatus(Status.SUCCESS);
            response.setMessage("Email Otp Verification Successful");

            response.setToken("access-token");
            response.setRefreshToken("refresh-token");

            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        response.setStatus(Status.FAILURE);
        response.setMessage("Email Otp Verification failed");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
