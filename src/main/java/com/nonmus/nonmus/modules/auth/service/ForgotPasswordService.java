package com.nonmus.nonmus.modules.auth.service;

import com.nonmus.nonmus.modules.common.exception.InvalidVerificationTokenException;
import com.nonmus.nonmus.modules.common.exception.UserNotFoundException;
import com.nonmus.nonmus.modules.user.entity.Users;
import com.nonmus.nonmus.modules.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ForgotPasswordService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private static final Duration RESET_TOKEN_EXPIRY = Duration.ofMinutes(10);
    private static final Duration OTP_EXPIRY = Duration.ofMinutes(10);

    private final StringRedisTemplate redisTemplate;
    private final UsersRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public String createVerificationCode(Users user) {

        String otp = String.format("%06d", RANDOM.nextInt(1_000_000));

        String key = getOtpKey(user.getEmail());

        redisTemplate.opsForValue().set(key, otp, OTP_EXPIRY);

        return otp;
    }

    public String verifyCode(String email, String otp) {

        String storedOtp =
                redisTemplate.opsForValue().get(getOtpKey(email));

        if (storedOtp == null)
            throw new RuntimeException("OTP expired");

        if (!storedOtp.equals(otp))
            throw new RuntimeException("Invalid OTP");

        redisTemplate.delete(getOtpKey(email));

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        String resetToken = UUID.randomUUID().toString();

        redisTemplate.opsForValue().set(
                getResetTokenKey(resetToken),
                user.getId().toString(),
                RESET_TOKEN_EXPIRY
        );

        return resetToken;
    }

    public void resetPassword(
            String resetToken,
            String newPassword) {

        String userId = redisTemplate.opsForValue()
                .get(getResetTokenKey(resetToken));

        if (userId == null) {
            throw new InvalidVerificationTokenException("Invalid or expired reset token");
        }

        Users user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);

        redisTemplate.delete(getResetTokenKey(resetToken));
    }

    public boolean isResetTokenValid(String email, String token) {

        String stored =
                redisTemplate.opsForValue().get(getResetTokenKey(email));

        return stored != null && stored.equals(token);
    }

    public void removeResetToken(String email) {
        redisTemplate.delete(getResetTokenKey(email));
    }

    public void removeOtp(String email) {
        redisTemplate.delete(getOtpKey(email));
    }

    private String getOtpKey(String email) {
        return "forgot-password:otp:" + email;
    }

    private String getResetTokenKey(String email) {
        return "forgot-password:reset:" + email;
    }
}