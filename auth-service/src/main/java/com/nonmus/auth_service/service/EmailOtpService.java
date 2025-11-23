package com.nonmus.auth_service.service;

import com.nonmus.auth_service.dto.SendEmailOtpRequest;
import com.nonmus.auth_service.dto.VerifyEmailOtpRequest;
import com.nonmus.auth_service.entity.User;
import com.nonmus.auth_service.exception.InvalidOtpException;
import com.nonmus.auth_service.exception.RateLimitException;
import com.nonmus.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailOtpService {
    private static final int OTP_LEN = 6;
    private static final long OTP_VALIDITY_MINUTES = 5;
    private static final long OTP_LIMIT_HOURS = 5;
    private static final int MAX_OTPS_ALLOWED = 5;
    private static final int MAX_RETRIES_OTP = 5;
    private static final long OTP_RETRIES_LIMIT_HOURS = 1;

    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;

    public void sendEmailOtp(SendEmailOtpRequest request) {
        String email = request.getEmail();

        var userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            log.error("User with email " + email + " does not exist");
            return;
        }

        log.info("Sending Email Otp for email " + email);

        User user = userOpt.get();
        if (Boolean.TRUE.equals(user.getIsEmailVerified())) {
            log.info("User with email " + email + " is already verified");
            return;
        }

        String otp = generateOtp();
        saveOtpInCache(email, otp);

        // TODO: Implement the actual email sending logic here

        log.info("Generated OTP for " + email + " is: " + otp); // For testing
    }

    private String generateOtp() {
        StringBuilder otp = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < EmailOtpService.OTP_LEN; i++) {
            otp.append(random.nextInt(10)); // 0 to 9
        }

        return otp.toString();
    }

    private void saveOtpInCache(String email, String otp) {
        log.info("Saving Otp for email " + email + " in Cache");
        if (!isOtpRequestAllowed(email)) {
            // Handle the case where the limit is exceeded
            log.info("Otp not Saved for email " + email + " due to rate limit");
            throw new RateLimitException("Rate limit exceeded: Max 5 OTPs per 5 hours.");
        }

        String otpKey = "otp:" + email;
        redisTemplate.opsForValue().set(otpKey, otp, Duration.ofMinutes(OTP_VALIDITY_MINUTES));
        log.info("Otp Saved for email " + email + " in Cache");
    }

    private boolean isOtpRequestAllowed(String email) {
        String limitKey = "otp:limit:" + email;
        ValueOperations<String, String> ops = redisTemplate.opsForValue();

        // Atomically increment the request count for the key
        Long currentCount = ops.increment(limitKey, 1);

        if (currentCount == null) {
            // Should not happen if Redis is available, but for safety
            return false;
        }

        // If it's the first request, set the expiration time for the tracking key
        if (currentCount == 1) {
            redisTemplate.expire(limitKey, Duration.ofHours(OTP_LIMIT_HOURS));
        }

        // Check if the count exceeds the maximum allowed
        return currentCount <= MAX_OTPS_ALLOWED;
    }

    public boolean verifyEmailOtp(VerifyEmailOtpRequest request) {
        log.info("Verifying Otp");
        String email = request.getEmail();
        String inputOtp = request.getOtp();

        var userOpt = userRepository.findByEmail(email);

        if (!isVerifyEmailOtpRequestAllowed(email)) {
            log.info("Rate limit exceeded for OTP verification for email " + email);
            throw new RateLimitException("Rate limit exceeded: Max 5 Retries per 1 hour.");
        }

        if (userOpt.isEmpty()) {
            log.info("User with email " + email + " does not exist");
            return false;
        }

        User user = userOpt.get();
        if (Boolean.TRUE.equals(user.getIsEmailVerified())) {
            log.info("User with email " + email + " is already verified");
            return false;
        }

        String otpKey = "otp:" + email;
        String storedOtp = redisTemplate.opsForValue().get(otpKey);

        if (storedOtp == null) {
            log.info("OTP is invalid or has expired for email " + email);
            throw new InvalidOtpException("OTP is invalid or has expired. Please request a new one.");
        }

        if (inputOtp.equals(storedOtp)) {
            String retriesOtpKey = "otp:retry:" + email;
            String limitKey = "otp:limit:" + email;

            log.info("OTP verified successfully for email " + email);
            redisTemplate.delete(otpKey);
            redisTemplate.delete(retriesOtpKey);
            redisTemplate.delete(limitKey);

            log.info("deleted otp related keys from cache for email " + email);
            updateUserEmailVerificationFlag(email);
            log.info("Email verification flag updated for email " + email);

            return true;
        }

        log.info("OTP verification failed for email " + email);
        return false;
    }

    private boolean isVerifyEmailOtpRequestAllowed(String email) {
        String otpRetriesKey = "otp:retry:" + email;
        String otpKey = "otp:" + email;

        String storedOtp = redisTemplate.opsForValue().get(otpKey);
        if (storedOtp == null || storedOtp.isEmpty()) {
            log.info("OTP is invalid or has expired for email " + email + " during retry check");
            throw new InvalidOtpException("OTP is invalid or has expired. Please request a new one.");
        }

        Long retriesCount = redisTemplate.opsForValue().increment(otpRetriesKey, 1);

        if (retriesCount == null) {
            log.error("Failed to increment OTP in Cache for email " + email);
            // Should not happen if Redis is available, but for safety
            return false;
        }

        // If it's the first request, set the expiration time for the tracking key
        if (retriesCount == 1) {
            log.info("Setting expiration for OTP retry key for email " + email);
            redisTemplate.expire(otpRetriesKey, Duration.ofHours(OTP_RETRIES_LIMIT_HOURS));
        }

        return retriesCount <= MAX_RETRIES_OTP;
    }

    private void updateUserEmailVerificationFlag(String email) {
        log.info("Updating email verification flag for email " + email);
        var userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            log.info("User with email " + email + " does not exist");
            return;
        }

        User user = userOpt.get();
        user.setIsEmailVerified(true);

        userRepository.save(user);
    }
}
