package com.nonmus.nonmus.modules.auth.controller;

import com.nonmus.nonmus.modules.auth.dto.request.ForgotPasswordRequest;
import com.nonmus.nonmus.modules.auth.dto.request.ForgotPasswordVerifyRequest;
import com.nonmus.nonmus.modules.auth.dto.request.ResetPasswordRequest;
import com.nonmus.nonmus.modules.auth.dto.response.ForgotPasswordResponse;
import com.nonmus.nonmus.modules.auth.dto.response.ForgotPasswordVerifyResponse;
import com.nonmus.nonmus.modules.auth.service.AuthenticationService;
import com.nonmus.nonmus.modules.auth.service.ForgotPasswordService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/forgot-password")
@RequiredArgsConstructor
public class ForgotPasswordController {
    private final AuthenticationService authService;
    private final ForgotPasswordService forgotPasswordService;


    @PostMapping("/send")
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);

        ForgotPasswordResponse response = new ForgotPasswordResponse();
        response.setMessage("Verification code sent to your email");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<ForgotPasswordVerifyResponse> forgotPasswordVerify(@RequestBody ForgotPasswordVerifyRequest request, HttpServletResponse response) {
        String resetToken = forgotPasswordService.verifyCode(request.getEmail(), request.getCode());

        ResponseCookie cookie = ResponseCookie.from("reset_token", resetToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Lax")
                .maxAge(Duration.ofMinutes(10))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        ForgotPasswordVerifyResponse verifyResponse = new ForgotPasswordVerifyResponse();
        verifyResponse.setMessage("Verification Successful");

        return ResponseEntity.ok(verifyResponse);
    }

    @PostMapping("/reset")
    public ResponseEntity<Void> resetPassword(
            @CookieValue("reset_token") String resetToken,
            @Valid @RequestBody ResetPasswordRequest request,
            HttpServletResponse response) {

        forgotPasswordService.resetPassword(
                resetToken,
                request.getNewPassword());

        ResponseCookie deleteCookie = ResponseCookie.from("reset_token", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api/v1/auth/forgot-password")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());

        return ResponseEntity.noContent().build();
    }
}
