package com.nonmus.factory;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.databind.introspect.TypeResolutionContext.Empty;
import com.nonmus.constants.AppConstants;
import com.nonmus.dto.ApiResponse;
import com.nonmus.dto.Errors;
import com.nonmus.dto.Meta;
import com.nonmus.dto.OtpRetryInfo;
import com.nonmus.service.OtpCacheService;


public class EmailOtpSendResponseFactory {

    public static ApiResponse<?> getApiResponse(String code, UUID userId, OtpCacheService otpCacheService) {
        if(code.equals(AppConstants.OTP_COOLDOWN_ACTIVE)) {
            long coolDownPeriod =  otpCacheService.getCooldownRemaining(userId);
            String otpCoolDownActiveMessage = String.format("You must wait %s seconds before requesting another OTP", 
                                            coolDownPeriod);
            
            ApiResponse<OtpRetryInfo> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setStatusCode(429);
            response.setMessage(otpCoolDownActiveMessage);

            OtpRetryInfo otpRetryInfo = new OtpRetryInfo();
            otpRetryInfo.setRetryAfterSeconds(coolDownPeriod);
            response.setData(otpRetryInfo);

            Meta meta = new Meta();
            meta.setTimeStamp(Instant.now());
            response.setMeta(meta);

            Errors otpCoolDownError = new Errors();
            otpCoolDownError.setCode("OTP_COOLDOWN_ACTIVE");
            otpCoolDownError.setMessage(otpCoolDownActiveMessage);
            response.setErrors(otpCoolDownError);

            return response;
        }
        else if(code.equals(AppConstants.OTP_RATE_LIMIT_EXCEEDED)) {
            long timeLeftToExpireDailyCount = otpCacheService.getDailyCountRemaining(userId);
            String otpRateLimitMessage = "Maximum OTP requests reached. Try again after the cooldown period";

            ApiResponse<OtpRetryInfo> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setStatusCode(429);
            response.setMessage(otpRateLimitMessage);

            OtpRetryInfo otpRetryInfo = new OtpRetryInfo();
            otpRetryInfo.setRetryAfterSeconds(timeLeftToExpireDailyCount);
            response.setData(otpRetryInfo);

            Meta meta = new Meta();
            meta.setTimeStamp(Instant.now());
            response.setMeta(meta);

            Errors otpCoolDownError = new Errors();
            otpCoolDownError.setCode("OTP_RATE_LIMIT_EXCEEDED");
            otpCoolDownError.setMessage(otpRateLimitMessage);
            response.setErrors(otpCoolDownError);
            return response;
        }
        else if(code.equals(AppConstants.SENT)) {
            ApiResponse<Empty> response = new ApiResponse<>();
            response.setSuccess(true);
            response.setStatusCode(200);
            response.setMessage("Otp sent successfully");
            
            Meta meta = new Meta();
            meta.setTimeStamp(Instant.now());
            response.setMeta(meta);

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
        errors.setMessage("Unable to send mail");

        response.setErrors(errors);

        return response;
    }
}
