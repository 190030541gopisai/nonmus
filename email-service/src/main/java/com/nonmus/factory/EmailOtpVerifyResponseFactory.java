package com.nonmus.factory;

import java.time.Instant;
import java.util.UUID;

import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.databind.introspect.TypeResolutionContext.Empty;
import com.nonmus.constants.AppConstants;
import com.nonmus.dto.ApiResponse;
import com.nonmus.dto.EmailOtpVerifyResponse;
import com.nonmus.dto.Errors;
import com.nonmus.dto.Meta;
import com.nonmus.dto.OtpRetryInfo;
import com.nonmus.dto.OtpVerifyAttemptsRemaining;
import com.nonmus.dto.TokenData;
import com.nonmus.dto.UserData;
import com.nonmus.dto.UserTokenInfo;
import com.nonmus.service.OtpCacheService;

import ch.qos.logback.core.subst.Token;

public class EmailOtpVerifyResponseFactory {
    
    private static TokenData generateTokenDataForUser(UUID userId) {
        // Implement token generation logic here
        // This is a placeholder implementation and should be replaced with actual token generation logic
        TokenData tokenData = new TokenData();
        tokenData.setAccessToken("generatedAccessToken");
        tokenData.setRefreshToken("generatedRefreshToken");
        tokenData.setAccessTokenExpiryInSeconds(3600); // Access token valid for 1 hour
        tokenData.setRefreshTokenExpiryInSeconds(86400); // Refresh token valid for 24 hours
        return tokenData;
    }

    public static ApiResponse<?> getApiResponse(EmailOtpVerifyResponse emailOtpVerifyResponse, UUID userId, OtpCacheService otpCacheService) {
        String code = emailOtpVerifyResponse.getCode();

        if(code.equals(AppConstants.INVALID_OTP) || 
           code.equals(AppConstants.USER_NOT_FOUND)) {

            if(otpCacheService.isVerifyCooldownActive(userId)) {
                ApiResponse<OtpRetryInfo> response = new ApiResponse<>();

                long cooldownRemaining = otpCacheService.getOtpVerifyCooldownRemaining(userId);

                response.setSuccess(false);
                response.setStatusCode(429);
                response.setMessage("Too many failed attempts");

                Meta meta = new Meta();
                meta.setTimeStamp(Instant.now());

                response.setMeta(meta);

                OtpRetryInfo retryInfo = new OtpRetryInfo();
                retryInfo.setRetryAfterSeconds(cooldownRemaining);
                response.setData(retryInfo);

                Errors errors = new Errors();
                errors.setCode(AppConstants.INVALID_OTP);
                errors.setMessage("You have exceeded the allowed number of attempts. Please try again later");

                response.setErrors(errors);

                return response;
            }

            Long attempts = otpCacheService.incrementVerifyAttempts(userId);
            
            if(otpCacheService.hasExceededVerifyAttempts(attempts)) {
                otpCacheService.applyVerifyCooldown(userId);
                otpCacheService.clearVerifyAttempts(userId);

                return getApiResponse(emailOtpVerifyResponse, userId, otpCacheService);
            }

            ApiResponse<OtpVerifyAttemptsRemaining> response = new ApiResponse<>();
            OtpVerifyAttemptsRemaining attemptsRemaining = new OtpVerifyAttemptsRemaining();
            attemptsRemaining.setAttemptsRemaining(otpCacheService.getOtpVerifyAttemptsRemaining(userId));
            response.setSuccess(false);
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("OTP is incorrect");
            response.setData(attemptsRemaining);

            Meta meta = new Meta();
            meta.setTimeStamp(Instant.now());

            response.setMeta(meta);

            Errors errors = new Errors();
            errors.setCode(AppConstants.INVALID_OTP);
            errors.setMessage("Otp is incorrect");

            response.setErrors(errors);

            return response;
        }
        else if(code.equals(AppConstants.OTP_VERIFIED)) {
            ApiResponse<UserTokenInfo> response = new ApiResponse<>();
            response.setSuccess(true);
            response.setStatusCode(200);
            response.setMessage("OTP verified successfully");
            
            // Todo: Add user object, token details (accessToke, refreshToken, accessTokenExpiry, refreshTokenExpiry) in response data    
            UserTokenInfo userTokenInfo = new UserTokenInfo();

            UserData userData = emailOtpVerifyResponse.getUserData();
            TokenData tokenData = emailOtpVerifyResponse.getTokenData();
            userTokenInfo.setUser(userData);
            userTokenInfo.setToken(tokenData);

            response.setData(userTokenInfo);

            Meta meta = new Meta();
            meta.setTimeStamp(Instant.now());

            response.setMeta(meta);

            otpCacheService.clearVerifyAttempts(userId);

            return response;
        }

        ApiResponse<Empty> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setStatusCode(500);
        response.setMessage("Internal server error");
        response.setData(null);

        Meta meta = new Meta();
        meta.setTimeStamp(Instant.now());

        response.setMeta(meta);

        Errors errors = new Errors();
        errors.setCode("INTERNAL_SERVER_ERROR");
        errors.setMessage("Unable to verify mail");

        response.setErrors(errors);

        return response;
    }
}
