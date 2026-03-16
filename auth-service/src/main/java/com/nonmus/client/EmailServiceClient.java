package com.nonmus.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.nonmus.dto.ApiResponse;
import com.nonmus.dto.EmailOtpSendRequest;

@FeignClient(
    name = "email-service",
    url = "${services.email.url}")
public interface EmailServiceClient {

    @PostMapping("/api/v1/email/otp/send")
    ApiResponse<?> sendOtp(@RequestBody EmailOtpSendRequest request);
}
