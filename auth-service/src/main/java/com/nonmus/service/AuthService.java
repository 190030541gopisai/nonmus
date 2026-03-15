package com.nonmus.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nonmus.client.EmailServiceClient;
import com.nonmus.client.UserServiceClient;
import com.nonmus.constants.AppConstants;
import com.nonmus.dto.ApiResponse;
import com.nonmus.dto.EmailOtpSendRequest;
import com.nonmus.dto.Errors;
import com.nonmus.dto.Meta;
import com.nonmus.dto.RegisterRequest;
import com.nonmus.dto.RegisterResponse;
import com.nonmus.dto.UserCreateRequest;
import com.nonmus.dto.UserCreateResponse;
import com.nonmus.exception.DownstreamServiceException;

import feign.FeignException;
import feign.Response;

@Service
public class AuthService {

    private final UserServiceClient userServiceClient;
    private final EmailServiceClient emailServiceClient;
    private final ObjectMapper objectMapper;

    public AuthService(UserServiceClient userServiceClient, EmailServiceClient emailServiceClient, ObjectMapper objectMapper) {
        this.userServiceClient = userServiceClient;
        this.emailServiceClient = emailServiceClient;
        this.objectMapper = objectMapper;
    }

    public ApiResponse<RegisterResponse> register(RegisterRequest request) {
        UserCreateResponse createdUser = createUser(request);
        OtpDispatchResult otpResult = sendOtp(createdUser);

        ApiResponse<RegisterResponse> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setStatusCode(201);
        response.setMessage(otpResult.isOtpSent() ? "Registration successful. OTP sent to email" : "Registration successful. OTP not sent");

        RegisterResponse registerResponse = new RegisterResponse();
        registerResponse.setUserId(createdUser.getUserId());
        registerResponse.setEmail(createdUser.getEmail());
        registerResponse.setFirstName(createdUser.getFirstName());
        registerResponse.setLastName(createdUser.getLastName());
        registerResponse.setEmailVerified(createdUser.isEmailVerified());
        registerResponse.setOtpSent(otpResult.isOtpSent());
        registerResponse.setOtpStatusCode(otpResult.getCode());
        registerResponse.setOtpMessage(otpResult.getMessage());

        response.setData(registerResponse);

        Meta meta = new Meta();
        meta.setTimeStamp(Instant.now());
        response.setMeta(meta);

        return response;
    }

    private UserCreateResponse createUser(RegisterRequest request) {
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setFirstName(request.getFirstName());
        userCreateRequest.setLastName(request.getLastName());
        userCreateRequest.setEmail(request.getEmail());
        userCreateRequest.setPassword(request.getPassword());

        ApiResponse<UserCreateResponse> userResponse = userServiceClient.createUser(userCreateRequest);
        if (userResponse == null || userResponse.getData() == null) {
            throw new DownstreamServiceException(AppConstants.USER_SERVICE_UNAVAILABLE, "Invalid response from user-service", 503);
        }

        return userResponse.getData();
    }

    private OtpDispatchResult sendOtp(UserCreateResponse createdUser) {
        EmailOtpSendRequest otpSendRequest = new EmailOtpSendRequest();
        otpSendRequest.setUserId(createdUser.getUserId());
        otpSendRequest.setEmail(createdUser.getEmail());

        try {
            Response response = emailServiceClient.sendOtp(otpSendRequest);
            return mapOtpResponse(response.status(), readBody(response));
        } catch (FeignException feignException) {
            return mapOtpResponse(feignException.status(), feignException.contentUTF8());
        } catch (IOException ioException) {
            return new OtpDispatchResult(false, AppConstants.OTP_NOT_SENT, "Unable to parse OTP response");
        }
    }

    private String readBody(Response response) throws IOException {
        if (response.body() == null) {
            return "";
        }

        return new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    private OtpDispatchResult mapOtpResponse(int statusCode, String body) {
        if (statusCode >= 200 && statusCode < 300) {
            String message = readApiMessage(body, "Otp sent successfully");
            return new OtpDispatchResult(true, AppConstants.OTP_SENT, message);
        }

        String message = readApiMessage(body, "OTP could not be sent right now. Please try after some time or contact support if the issue persists.");
        String errorCode = readApiErrorCode(body, AppConstants.OTP_NOT_SENT);
        return new OtpDispatchResult(false, errorCode, message);
    }

    private String readApiMessage(String body, String fallback) {
        if (body == null || body.isBlank()) {
            return fallback;
        }

        try {
            ApiResponse<Object> parsed = objectMapper.readValue(body, new TypeReference<ApiResponse<Object>>() {
            });
            if (parsed.getMessage() != null && !parsed.getMessage().isBlank()) {
                return parsed.getMessage();
            }
        } catch (Exception ignored) {
            return fallback;
        }

        return fallback;
    }

    private String readApiErrorCode(String body, String fallback) {
        if (body == null || body.isBlank()) {
            return fallback;
        }

        try {
            ApiResponse<Object> parsed = objectMapper.readValue(body, new TypeReference<ApiResponse<Object>>() {
            });
            Errors errors = parsed.getErrors();
            if (errors != null && errors.getCode() != null && !errors.getCode().isBlank()) {
                return errors.getCode();
            }
        } catch (Exception ignored) {
            return fallback;
        }

        return fallback;
    }

    private static class OtpDispatchResult {
        private final boolean otpSent;
        private final String code;
        private final String message;

        OtpDispatchResult(boolean otpSent, String code, String message) {
            this.otpSent = otpSent;
            this.code = code;
            this.message = message;
        }

        boolean isOtpSent() {
            return otpSent;
        }

        String getCode() {
            return code;
        }

        String getMessage() {
            return message;
        }
    }
}
