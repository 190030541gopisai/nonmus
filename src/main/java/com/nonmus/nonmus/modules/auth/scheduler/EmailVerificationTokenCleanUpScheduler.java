package com.nonmus.nonmus.modules.auth.scheduler;


import com.nonmus.nonmus.modules.auth.repository.EmailVerificationTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class EmailVerificationTokenCleanUpScheduler {

    private EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Scheduled(cron = "0 0 * * * *")
    public void cleanup() {
        emailVerificationTokenRepository.deleteAllByExpiresAtBefore(LocalDateTime.now());
    }
}
