package com.nonmus.nonmus.modules.auth.scheduler;


import com.nonmus.nonmus.modules.auth.repository.EmailVerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class EmailVerificationTokenCleanUpScheduler {

    private final EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Scheduled(cron = "0 0 * * * *")
    public void cleanup() {
        emailVerificationTokenRepository.deleteAllByExpiresAtBefore(LocalDateTime.now());
    }
}
