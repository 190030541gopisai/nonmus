package com.nonmus.nonmus.modules.auth.listener;

import com.nonmus.nonmus.modules.auth.events.EmailVerificationEvent;
import com.nonmus.nonmus.modules.auth.service.MailService;
import com.nonmus.nonmus.modules.auth.service.VerificationTokenService;
import com.nonmus.nonmus.modules.user.entity.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service(value = "emailVerificationService")
@RequiredArgsConstructor
public class SendEmailVerificationListener{
    private final MailService mailService;
    private final VerificationTokenService tokenService;

    @Value("${app.email.verification.link}")
    private String emailVerificationLink;

    @Async
    @EventListener
    @Transactional
    public void sendEmailVerificationLinkWithToken(EmailVerificationEvent event){
        Users user = event.getUser();

        String token = tokenService.createToken(user);

        String verificationLink =
                emailVerificationLink + token;

        String content = """
                Hello %s,

                Click below to verify your email.

                %s

                Link expires in 24 hours.
                """
                .formatted(user.getEmail(), verificationLink);

        String email = user.getEmail();

        log.info("Sending verification email to: " + email);

        System.out.println(content);
        // mailService.send(user.getEmail(), content);

        log.info("Verification email sent to: " + email);
    }
}
