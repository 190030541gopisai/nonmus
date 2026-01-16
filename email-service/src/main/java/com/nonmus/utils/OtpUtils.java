package com.nonmus.utils;

import java.security.SecureRandom;

public class OtpUtils {
    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateOtp() {
        int otp = secureRandom.nextInt(1_000_000); // 0 to 999999
        return String.format("%06d", otp);
    }
}
