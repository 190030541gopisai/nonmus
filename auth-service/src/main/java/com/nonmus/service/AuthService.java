package com.nonmus.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nonmus.client.EmailServiceClient;
import com.nonmus.client.UserServiceClient;
import com.nonmus.constants.AppConstants;
import com.nonmus.dto.ApiResponse;
import com.nonmus.dto.EmailOtpSendRequest;
import com.nonmus.dto.EmailOtpVerifyRequest;
import com.nonmus.dto.Errors;
import com.nonmus.dto.Meta;
import com.nonmus.dto.RegisterRequest;
import com.nonmus.dto.RegisterResponse;
import com.nonmus.dto.UserCreateRequest;
import com.nonmus.dto.UserCreateResponse;
import com.nonmus.exception.DownStreamServiceException;
import com.nonmus.exception.UserAlreadyExistsException;

import feign.FeignException;
import feign.Response;
import feign.RetryableException;

@Service
public class AuthService {

    private final UserServiceClient userServiceClient;
    private final EmailServiceClient emailServiceClient;

    public AuthService(UserServiceClient userServiceClient, EmailServiceClient emailServiceClient) {
        this.userServiceClient = userServiceClient;
        this.emailServiceClient = emailServiceClient;
    }

    public ApiResponse<RegisterResponse> register(RegisterRequest request) {
        ApiResponse<RegisterResponse> response = new ApiResponse<>();
        Meta meta = new Meta();
        meta.setTimeStamp(Instant.now());
        response.setMeta(meta);

        UserCreateResponse userResponse = createUser(request, response);

        if(userResponse == null) {
            return response;
        }
        
        RegisterResponse registerResponse = new RegisterResponse();
        registerResponse.setUserId(userResponse.getUserId());
        registerResponse.setEmail(userResponse.getEmail());
        registerResponse.setFirstName(userResponse.getFirstName());
        registerResponse.setLastName(userResponse.getLastName());
        registerResponse.setEmailVerified(userResponse.isEmailVerified());


        boolean otpSent = sendOtp(userResponse.getUserId(), userResponse.getEmail(), response);
        registerResponse.setOtpSent(otpSent);

        response.setSuccess(true);
        response.setStatusCode(201);
        response.setMessage("User registered successfully");
        response.setData(registerResponse); 

        return response;
    }

    private UserCreateResponse createUser(RegisterRequest request, ApiResponse<RegisterResponse> response) {
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setFirstName(request.getFirstName());
        userCreateRequest.setLastName(request.getLastName());
        userCreateRequest.setEmail(request.getEmail());
        userCreateRequest.setPassword(request.getPassword());
        
        try {
            ApiResponse<UserCreateResponse> userResponse = userServiceClient.createUser(userCreateRequest);
            return userResponse.getData();
        } catch (RetryableException ex) {
            response.setSuccess(false);
            response.setStatusCode(503);
            response.setMessage("User service is currently unavailable. Please try again later.");

            Errors errors = new Errors();
            errors.setCode("USER_SERVICE_UNAVAILABLE");
            errors.setMessage("User service is unavailable");

            response.setErrors(errors);
            return null;
        } catch (FeignException ex) {
            response.setSuccess(false);
            response.setStatusCode(502);
            response.setMessage("User service returned an error: " + ex.getMessage());

            Errors errors = new Errors();
            errors.setCode("USER_SERVICE_ERROR");
            errors.setMessage("User service returned an error: " + ex.getMessage());

            response.setErrors(errors);
            return null;
        } 
    }

    private boolean sendOtp(UUID userId, String email, ApiResponse<RegisterResponse> response) {
        EmailOtpSendRequest otpRequest = new EmailOtpSendRequest();
        otpRequest.setUserId(userId);
        otpRequest.setEmail(email);
        
        try{
            ApiResponse<?> apiResponse = emailServiceClient.sendOtp(otpRequest);
            return apiResponse.isSuccess();
        } catch(RetryableException ex) {
            Errors errors = new Errors();
            errors.setCode("EMAIL_SERVICE_UNAVAILABLE");
            errors.setMessage("Email service is unavailable");

            response.setErrors(errors);
            return false;
        }
        catch(FeignException ex) {
            Errors errors = new Errors();
            errors.setCode("EMAIL_SERVICE_ERROR");
            errors.setMessage("Email service returned an error");

            response.setErrors(errors);
            return false;
        }
    }

    public ApiResponse<?> verifyOtp(EmailOtpVerifyRequest request) {
        try{
            ApiResponse<?> response = emailServiceClient.verifyOtp(request);
            return response;   
        } catch(RetryableException ex) {
            ApiResponse<?> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setStatusCode(503);
            response.setMessage("Email service is currently unavailable. Please try again later.");

            Errors errors = new Errors();
            errors.setCode("EMAIL_SERVICE_UNAVAILABLE");
            errors.setMessage("Email service is unavailable");

            response.setErrors(errors);
            return response;
        }
        catch(FeignException ex) {
            ApiResponse<?> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setStatusCode(502);
            response.setMessage("Email service returned an error: " + ex.getMessage());

            Errors errors = new Errors();
            errors.setCode("EMAIL_SERVICE_ERROR");
            errors.setMessage("Email service returned an error: " + ex.getMessage());

            response.setErrors(errors);
            return response;
        } 
    }

}


