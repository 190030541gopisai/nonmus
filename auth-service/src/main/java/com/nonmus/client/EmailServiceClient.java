package com.nonmus.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.nonmus.decoder.EmailServiceFeignErrorDecoder;
import com.nonmus.dto.ApiResponse;
import com.nonmus.dto.EmailOtpSendRequest;
import com.nonmus.dto.EmailOtpVerifyRequest;

@FeignClient(
    name = "email-service",
    url = "${services.email.url}",
    configuration = EmailServiceFeignErrorDecoder.class)
public interface EmailServiceClient {

    @PostMapping("/api/v1/email/otp/send")
    ApiResponse<?> sendOtp(@RequestBody EmailOtpSendRequest request);

    @PostMapping("/api/v1/email/otp/verify")
    ApiResponse<?> verifyOtp(@RequestBody EmailOtpVerifyRequest request);
}
