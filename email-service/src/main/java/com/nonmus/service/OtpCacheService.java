package com.nonmus.service;

import java.time.Duration;
import java.util.UUID;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class OtpCacheService {
    private static final int MAX_OTP_LIFE_IN_MINUTES = 5;
    private static final int MAX_OTP_RATE_LIMIT_TIME_IN_HOURS = 24;

    private final StringRedisTemplate redisTemplate;

    public OtpCacheService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveOtp(UUID userId, String otp) {
        String key = "otp:" + userId;

        redisTemplate.opsForValue()
                .set(key, otp, Duration.ofMinutes(MAX_OTP_LIFE_IN_MINUTES));
    }

    public String getOtp(UUID userId) {
        String key = "otp:" + userId;

        return redisTemplate.opsForValue()
                .get(key);
    }

    public void deleteOtp(UUID userId) {
        String key = "otp:" + userId;
        
        redisTemplate.delete(key);
    }

    public void applyCooldown(UUID userId) {
        String key = "otp:cooldown:" + userId;

        redisTemplate.opsForValue()
                    .set(key, "1", Duration.ofSeconds(60));
    }

    public boolean isCooldownActive(UUID userId) {
        String key = "otp:cooldown:" + userId;

        return Boolean.TRUE.equals(
                redisTemplate.hasKey(key)
        );
    }

    public long getCooldownRemaining(UUID userId) {
        String key = "otp:cooldown:" + userId;
        return redisTemplate.getExpire(key);
    }

    public long incrementDailyCount(UUID userId) {
        String key = "otp:count:" + userId;

        Long count = redisTemplate.opsForValue().increment(key);

        if (count != null && count == 1) {
            redisTemplate.expire(key, Duration.ofHours(MAX_OTP_RATE_LIMIT_TIME_IN_HOURS));
        }

        return count;
    }

    public long getDailyCountRemaining(UUID userId) {
        String key = "otp:count:" + userId;
        return redisTemplate.getExpire("otp:count:" + userId);
    }
}
