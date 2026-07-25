package com.nonmus.nonmus.modules.auth.service;

import com.nonmus.nonmus.modules.common.exception.InvalidOtpException;
import com.nonmus.nonmus.modules.common.exception.InvalidResetTokenException;
import com.nonmus.nonmus.modules.common.exception.OtpExpiredException;
import com.nonmus.nonmus.modules.common.exception.UserNotFoundException;
import com.nonmus.nonmus.modules.user.entity.Users;
import com.nonmus.nonmus.modules.user.repository.UsersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ForgotPasswordServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private UsersRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private ForgotPasswordService forgotPasswordService;

    @Captor
    private ArgumentCaptor<String> keyCaptor;

    @Captor
    private ArgumentCaptor<String> valueCaptor;

    @Captor
    private ArgumentCaptor<Duration> durationCaptor;

    @Test
    void createVerificationCode_shouldStoreOtpAndReturnIt() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        Users user = new Users();
        user.setEmail("test@test.com");

        String otp = forgotPasswordService.createVerificationCode(user);

        assertNotNull(otp);
        assertEquals(6, otp.length());
        assertTrue(otp.matches("\\d{6}"));

        verify(valueOperations).set(
                eq("forgot-password:otp:test@test.com"),
                eq(otp),
                eq(Duration.ofMinutes(10)));
    }

    @Test
    void verifyCode_shouldReturnResetToken_whenOtpIsValid() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("forgot-password:otp:test@test.com")).thenReturn("123456");

        Users user = new Users();
        user.setId(UUID.randomUUID());

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        String resetToken = forgotPasswordService.verifyCode("test@test.com", "123456");

        assertNotNull(resetToken);

        verify(redisTemplate).delete("forgot-password:otp:test@test.com");
        verify(valueOperations).set(
                startsWith("forgot-password:reset:"),
                eq(user.getId().toString()),
                eq(Duration.ofMinutes(10)));
    }

    @Test
    void verifyCode_shouldThrowOtpExpired_whenNoStoredOtp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("forgot-password:otp:test@test.com")).thenReturn(null);

        assertThrows(OtpExpiredException.class,
                () -> forgotPasswordService.verifyCode("test@test.com", "123456"));
    }

    @Test
    void verifyCode_shouldThrowInvalidOtp_whenOtpDoesNotMatch() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("forgot-password:otp:test@test.com")).thenReturn("654321");

        assertThrows(InvalidOtpException.class,
                () -> forgotPasswordService.verifyCode("test@test.com", "123456"));
    }

    @Test
    void verifyCode_shouldThrowUserNotFound_whenUserDoesNotExistAfterOtpVerification() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("forgot-password:otp:test@test.com")).thenReturn("123456");
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> forgotPasswordService.verifyCode("test@test.com", "123456"));
    }

    @Test
    void resetPassword_shouldEncodeAndSave_whenTokenIsValid() {
        UUID userId = UUID.randomUUID();
        String resetToken = UUID.randomUUID().toString();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("forgot-password:reset:" + resetToken)).thenReturn(userId.toString());

        Users user = new Users();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedPassword");

        forgotPasswordService.resetPassword(resetToken, "newPassword123");

        assertEquals("encodedPassword", user.getPassword());
        verify(userRepository).save(user);
        verify(redisTemplate).delete("forgot-password:reset:" + resetToken);
    }

    @Test
    void resetPassword_shouldThrowInvalidResetToken_whenTokenNotFound() {
        String resetToken = UUID.randomUUID().toString();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("forgot-password:reset:" + resetToken)).thenReturn(null);

        assertThrows(InvalidResetTokenException.class,
                () -> forgotPasswordService.resetPassword(resetToken, "newPassword123"));
    }

    @Test
    void resetPassword_shouldThrowUserNotFound_whenUserDoesNotExist() {
        UUID userId = UUID.randomUUID();
        String resetToken = UUID.randomUUID().toString();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("forgot-password:reset:" + resetToken)).thenReturn(userId.toString());
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> forgotPasswordService.resetPassword(resetToken, "newPassword123"));
    }

    @Test
    void isResetTokenValid_shouldReturnTrue_whenTokenMatches() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("forgot-password:reset:test@test.com")).thenReturn("valid-token");

        assertTrue(forgotPasswordService.isResetTokenValid("test@test.com", "valid-token"));
    }

    @Test
    void isResetTokenValid_shouldReturnFalse_whenTokenDoesNotMatch() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("forgot-password:reset:test@test.com")).thenReturn("stored-token");

        assertFalse(forgotPasswordService.isResetTokenValid("test@test.com", "wrong-token"));
    }

    @Test
    void isResetTokenValid_shouldReturnFalse_whenNoStoredToken() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("forgot-password:reset:test@test.com")).thenReturn(null);

        assertFalse(forgotPasswordService.isResetTokenValid("test@test.com", "any-token"));
    }

    @Test
    void removeResetToken_shouldDeleteFromRedis() {
        forgotPasswordService.removeResetToken("test@test.com");

        verify(redisTemplate).delete("forgot-password:reset:test@test.com");
    }

    @Test
    void removeOtp_shouldDeleteFromRedis() {
        forgotPasswordService.removeOtp("test@test.com");

        verify(redisTemplate).delete("forgot-password:otp:test@test.com");
    }
}