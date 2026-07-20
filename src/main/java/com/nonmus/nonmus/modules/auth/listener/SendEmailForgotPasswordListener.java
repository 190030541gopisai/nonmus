package com.nonmus.nonmus.modules.auth.listener;

import com.nonmus.nonmus.modules.auth.events.EmailForgotPasswordEvent;
import com.nonmus.nonmus.modules.auth.service.ForgotPasswordService;
import com.nonmus.nonmus.modules.auth.service.MailService;
import com.nonmus.nonmus.modules.user.entity.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendEmailForgotPasswordListener {

    private final MailService mailService;
    private final ForgotPasswordService forgotPasswordService;

    @Async
    @EventListener
    public void sendForgotPasswordCode(EmailForgotPasswordEvent event) {

        Users user = event.getUser();

        String verificationCode = forgotPasswordService.createVerificationCode(user);

        String subject = "Reset Your Password";

        String content = """
                Hello %s,

                We received a request to reset the password for your Nonmus account.

                Your verification code is:

                %s

                This code expires in 10 minutes.

                If you didn't request a password reset, you can safely ignore this email.

                Regards,
                Nonmus Team
                """
                .formatted(user.getName(), verificationCode);

        String email = user.getEmail();

        log.info("Sending Forgot password verification code to: " + email);

        System.out.println(content);

//        mailService.send(
//                user.getEmail(),
//                subject,
//                content
//        );

        log.info("Sent Forgot password verification code to: " + email);
    }
}