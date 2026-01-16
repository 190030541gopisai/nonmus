package com.nonmus.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.nonmus.clients.UserServiceClient;
import com.nonmus.constants.AppConstants;
import com.nonmus.dto.EmailOtpSendRequest;
import com.nonmus.dto.EmailOtpSendResponse;
import com.nonmus.dto.UserValidateRequest;
import com.nonmus.dto.UserValidateResponse;
import com.nonmus.utils.OtpUtils;

@Service
public class EmailOtpService {
    private static final int MAX_OTPS = 1; 

    private final OtpCacheService otpCacheService;
    private final MailService mailService;
    private final UserServiceClient userServiceClient;
    
    public EmailOtpService(OtpCacheService otpCacheService, MailService mailService, 
        UserServiceClient userServiceClient) {

        this.otpCacheService = otpCacheService;
        this.mailService = mailService;
        this.userServiceClient = userServiceClient;
    }

    public EmailOtpSendResponse sendOtp(EmailOtpSendRequest request) {
        EmailOtpSendResponse response = new EmailOtpSendResponse();

        UUID userId = request.getUserId();
        String email = request.getEmail();

        UserValidateRequest userValidateRequest = new UserValidateRequest();
        userValidateRequest.setUserId(userId);
        userValidateRequest.setEmail(email);

        UserValidateResponse userValidateResponse = userServiceClient.validate(userValidateRequest);

        if(!userValidateResponse.isBelongsToSameUser()) {
            response.setCode(AppConstants.USER_NOT_FOUND);
            return response;
        }

        if(otpCacheService.isCooldownActive(userId)) {
            response.setCode(AppConstants.OTP_COOLDOWN_ACTIVE);
            return response;
        }
        
        if(otpCacheService.incrementDailyCount(userId) > MAX_OTPS) {
            response.setCode(AppConstants.OTP_RATE_LIMIT_EXCEEDED);
            return response;
        }

        
        String otp = OtpUtils.generateOtp();
        otpCacheService.saveOtp(userId, otp);
        
        // mailService.sendOtpMail(email, otp);
        
        otpCacheService.applyCooldown(userId);
        response.setCode(AppConstants.SENT);

        return response;
    }

}


// userid exists or not
// userid exists and match with email

//userid:otp:
//userid:otp:cooldown:
//userid:otp:limit: - when this limit becomes zero cooldown becomes active
//userid:otp:limit:cooldown

/**
 *
 * 
 * userid:otp:limit:cooldown -> otp-rate-limit-exceeded
 * userid:otp:cooldown: -> otp-cooldown-active
 * userid:otp:limit > 0 -> {
 *      if(userid found) { // only when user exists
 *          sendOtp()
 *      }
 * }
 * userid:otp:limit <= 0 -> otp-rate-limit-exceeded, activate userid:otp:limit:cooldown
 */