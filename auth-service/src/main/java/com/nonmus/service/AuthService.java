package com.nonmus.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.nonmus.client.EmailServiceClient;
import com.nonmus.client.UserServiceClient;
import com.nonmus.constants.AppConstants;
import com.nonmus.dto.ApiResponse;
import com.nonmus.dto.EmailNotVerifiedResponse;
import com.nonmus.dto.EmailOtpSendRequest;
import com.nonmus.dto.EmailOtpVerifyRequest;
import com.nonmus.dto.LoginResponse;
import com.nonmus.dto.Meta;
import com.nonmus.dto.RegisterRequest;
import com.nonmus.dto.RegisterResponse;
import com.nonmus.dto.TokenInfo;
import com.nonmus.dto.UserAuthRequest;
import com.nonmus.dto.UserCreateRequest;
import com.nonmus.dto.UserCreateResponse;
import com.nonmus.dto.UserData;
import com.nonmus.exception.DownStreamException;
import com.nonmus.util.JwtUtil;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;

@Service
public class AuthService {

    private final UserServiceClient userServiceClient;
    private final EmailServiceClient emailServiceClient;
    private final JwtUtil jwtUtil;

    public AuthService(UserServiceClient userServiceClient, EmailServiceClient emailServiceClient, JwtUtil jwtUtil) {
        this.userServiceClient = userServiceClient;
        this.emailServiceClient = emailServiceClient;
        this.jwtUtil = jwtUtil;
    }

    public ApiResponse<RegisterResponse> register(RegisterRequest request) {
        ApiResponse<RegisterResponse> response = new ApiResponse<>();

        Meta meta = new Meta();
        meta.setTimeStamp(Instant.now());

        response.setMeta(meta);

        UserCreateResponse userResponse = createUser(request);
        
        RegisterResponse registerResponse = new RegisterResponse();
        registerResponse.setUserId(userResponse.getUserId());
        registerResponse.setEmail(userResponse.getEmail());
        registerResponse.setFirstName(userResponse.getFirstName());
        registerResponse.setLastName(userResponse.getLastName());
        registerResponse.setEmailVerified(userResponse.isEmailVerified());

        boolean otpSent = sendOtp(userResponse.getUserId(), userResponse.getEmail());
        registerResponse.setOtpSent(otpSent);

        response.setSuccess(true);
        response.setStatusCode(201);
        response.setMessage("User registered successfully");
        response.setData(registerResponse); 

        return response;
    }

    private UserCreateResponse createUser(RegisterRequest request) {
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setFirstName(request.getFirstName());
        userCreateRequest.setLastName(request.getLastName());
        userCreateRequest.setEmail(request.getEmail());
        userCreateRequest.setPassword(request.getPassword());
        
        ApiResponse<UserCreateResponse> userResponse = userServiceClient.createUser(userCreateRequest);
        return userResponse.getData();
    }

    private boolean sendOtp(UUID userId, String email) {
        EmailOtpSendRequest otpRequest = new EmailOtpSendRequest();
        otpRequest.setUserId(userId);
        otpRequest.setEmail(email);
      
        ApiResponse<?> apiResponse = emailServiceClient.sendOtp(otpRequest);
        return apiResponse.isSuccess();
    }

    public ApiResponse<?> verifyOtp(EmailOtpVerifyRequest request) {
        ApiResponse<?> response = emailServiceClient.verifyOtp(request);

        if(response.isSuccess() && response.getMessage().equals(AppConstants.OTP_VERIFIED_SUCCESSFULLY)) {
            UserData userData = userServiceClient.updateEmailVerified(request.getUserId());
            TokenInfo tokenInfo = generateTokenInfo(userData);
            
            ApiResponse<TokenInfo> tokenResponse = new ApiResponse<>();
            tokenResponse.setSuccess(true);
            tokenResponse.setStatusCode(200);
            tokenResponse.setMessage(AppConstants.OTP_VERIFIED_SUCCESSFULLY);
            tokenResponse.setData(tokenInfo);

            Meta meta = new Meta();
            meta.setTimeStamp(Instant.now());
            tokenResponse.setMeta(meta);

            return tokenResponse;
        }

        return response;   
    }

    private TokenInfo generateTokenInfo(UserData userData) {
        String accessToken = jwtUtil.generateToken(userData.getUserId().toString());
        String refreshToken = jwtUtil.generateRefreshToken(userData.getUserId().toString());

        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setAccessToken(accessToken);
        tokenInfo.setRefreshToken(refreshToken);
        tokenInfo.setExpiresIn(jwtUtil.getTokenExpirationInSeconds()); // 1 hour expiry for access token
        return tokenInfo;
    }

    public ApiResponse<?> resendOtp(EmailOtpSendRequest request) {
        ApiResponse<?> response = emailServiceClient.sendOtp(request);
        return response;
    }

    public ApiResponse<?> login(UserAuthRequest request) {
        
        Meta meta = new Meta();
        meta.setTimeStamp(Instant.now());

        ResponseEntity<UserData> userDataResponse;
        
        UserData userData;
        
        try {
            userDataResponse = userServiceClient.authenticate(request);
            userData = userDataResponse.getBody();
        } catch (DownStreamException e) {
            if(e.getStatus() == 401) {
                ApiResponse<LoginResponse> response = new ApiResponse<>();
                response.setMeta(meta);
                response.setSuccess(false);
                response.setStatusCode(401);
                response.setMessage("Invalid email or password");
                return response;
            }
            throw e;
        }

        if(userData == null) {
            ApiResponse<LoginResponse> response = new ApiResponse<>();
            response.setMeta(meta);
            response.setSuccess(false);
            response.setStatusCode(401);
            response.setMessage("Invalid email or password");
            return response;
        }

        if(!userData.isEmailVerified()) {
            // Don't auto-resend OTP during login to avoid rate limit issues
            // Let user manually trigger resend on the OTP verification page
            
            ApiResponse<EmailNotVerifiedResponse> response = new ApiResponse<>();
            response.setMeta(meta);
            response.setSuccess(false);
            response.setStatusCode(403);
            response.setMessage("Email not verified. Please verify your email.");
            
            // Include userId in response so frontend can navigate to verify-otp page
            EmailNotVerifiedResponse data = new EmailNotVerifiedResponse();
            data.setUserId(userData.getUserId());
            data.setEmail(userData.getEmail());
            response.setData(data);
            
            return response;
        }
        
        ApiResponse<LoginResponse> response = new ApiResponse<>();
        response.setMeta(meta);
        
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setUser(userData);
        loginResponse.setTokenInfo(generateTokenInfo(userData));

        response.setSuccess(true);
        response.setStatusCode(200);
        response.setMessage("Login successful");
        response.setData(loginResponse);
    
        return response;
    }
}


