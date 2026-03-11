package com.nonmus.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.nonmus.clients.UserServiceClient;
import com.nonmus.constants.AppConstants;
import com.nonmus.dto.EmailOtpSendRequest;
import com.nonmus.dto.EmailOtpSendResponse;
import com.nonmus.dto.EmailOtpVerifyRequest;
import com.nonmus.dto.EmailOtpVerifyResponse;
import com.nonmus.dto.TokenData;
import com.nonmus.dto.UserData;
import com.nonmus.dto.UserTokenInfo;
import com.nonmus.dto.UserValidateRequest;
import com.nonmus.dto.UserValidateResponse;
import com.nonmus.exception.DownStreamServiceException;
import com.nonmus.utils.OtpUtils;

import feign.FeignException;
import feign.RetryableException;
import jakarta.validation.Valid;

@Service
public class EmailOtpService {
    private static final int MAX_OTPS = 1; 

    private final OtpCacheService otpCacheService;
    private final MailService mailService;
    private final UserServiceClient userServiceClient;
    private final TokenService tokenService;
    
    public EmailOtpService(OtpCacheService otpCacheService, MailService mailService, 
        UserServiceClient userServiceClient, TokenService tokenService) {

        this.otpCacheService = otpCacheService;
        this.mailService = mailService;
        this.userServiceClient = userServiceClient;
        this.tokenService = tokenService;
    }

    public EmailOtpSendResponse sendOtp(EmailOtpSendRequest request) {
        EmailOtpSendResponse response = new EmailOtpSendResponse();

        UUID userId = request.getUserId();
        String email = request.getEmail();

        UserValidateRequest userValidateRequest = new UserValidateRequest();
        userValidateRequest.setUserId(userId);
        userValidateRequest.setEmail(email);

        UserValidateResponse userValidateResponse;

        try {
            userValidateResponse = userServiceClient.validate(userValidateRequest);
        }  catch (RetryableException ex) {
            throw new DownStreamServiceException(
                    AppConstants.USER_SERVICE_UNAVAILABLE,
                    "User service is unavailable",
                    ex
            );
        } catch (FeignException ex) {
            throw new DownStreamServiceException(
                    AppConstants.USER_SERVICE_ERROR,
                    "User service returned an error",
                    ex
            );
        }
        
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
        
        // mailService.sendOtpMail(email, otp); // Real Sending mail
        System.out.println("OTP for user " + userId + " is: " + otp); // For testing purposes, print OTP to console
        
        otpCacheService.applyCooldown(userId);
        response.setCode(AppConstants.SENT);

        return response;
    }

    public EmailOtpVerifyResponse verifyOtp(EmailOtpVerifyRequest request) {
        EmailOtpVerifyResponse response = new EmailOtpVerifyResponse();

        UUID userId = request.getUserId();
        String email = request.getEmail();

        UserValidateRequest userValidateRequest = new UserValidateRequest();
        userValidateRequest.setUserId(userId);
        userValidateRequest.setEmail(email);

        UserValidateResponse userValidateResponse;

        try {
            userValidateResponse = userServiceClient.validate(userValidateRequest);
        }  catch (RetryableException ex) {
            throw new DownStreamServiceException(
                    AppConstants.USER_SERVICE_UNAVAILABLE,
                    "User service is unavailable",
                    ex
            );
        } catch (FeignException ex) {
            throw new DownStreamServiceException(
                    AppConstants.USER_SERVICE_ERROR,
                    "User service returned an error",
                    ex
            );
        }
        
        if(!userValidateResponse.isBelongsToSameUser()) {
            response.setCode(AppConstants.USER_NOT_FOUND);
            return response;
        }

        String cachedOtp = otpCacheService.getOtp(userId);    

        if (cachedOtp == null || !cachedOtp.equals(request.getOtp())) {
            response.setCode(AppConstants.INVALID_OTP);
            return response;
        }

        UserData userData = userServiceClient.getUserDataByUserId(userId);
        TokenData tokenData = tokenService.generateTokenDataForUser(userData);

        response.setCode(AppConstants.OTP_VERIFIED);
        response.setUserData(userData);
        response.setTokenData(tokenData);

        otpCacheService.deleteOtp(userId);
        // Todo: Update user record in user service to mark email as verified
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

/*

otp:verify:

*/
