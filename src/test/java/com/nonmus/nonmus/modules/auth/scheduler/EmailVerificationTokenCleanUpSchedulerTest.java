package com.nonmus.nonmus.modules.auth.scheduler;

import com.nonmus.nonmus.modules.auth.repository.EmailVerificationTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailVerificationTokenCleanUpSchedulerTest {

    @Mock
    private EmailVerificationTokenRepository emailVerificationTokenRepository;

    @InjectMocks
    private EmailVerificationTokenCleanUpScheduler scheduler;

    @Test
    void cleanup_shouldDeleteExpiredTokens() {
        scheduler.cleanup();

        verify(emailVerificationTokenRepository)
                .deleteAllByExpiresAtBefore(any(LocalDateTime.class));
    }
}