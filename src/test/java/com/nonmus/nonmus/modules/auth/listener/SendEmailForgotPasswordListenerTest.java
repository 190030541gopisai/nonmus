package com.nonmus.nonmus.modules.auth.listener;

import com.nonmus.nonmus.modules.auth.events.EmailForgotPasswordEvent;
import com.nonmus.nonmus.modules.auth.service.ForgotPasswordService;
import com.nonmus.nonmus.modules.auth.service.MailService;
import com.nonmus.nonmus.modules.user.entity.Users;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendEmailForgotPasswordListenerTest {

    @Mock
    private MailService mailService;

    @Mock
    private ForgotPasswordService forgotPasswordService;

    @InjectMocks
    private SendEmailForgotPasswordListener listener;

    @Captor
    private ArgumentCaptor<String> stringCaptor;

    @Test
    void sendForgotPasswordCode_shouldSendEmailWithCode() {
        Users user = new Users();
        user.setName("John");
        user.setEmail("john@test.com");

        when(forgotPasswordService.createVerificationCode(user))
                .thenReturn("123456");

        EmailForgotPasswordEvent event = new EmailForgotPasswordEvent(user);

        listener.sendForgotPasswordCode(event);

        verify(forgotPasswordService).createVerificationCode(user);
        verify(mailService).send(
                eq("john@test.com"),
                eq("Reset Your Password"),
                stringCaptor.capture());

        String content = stringCaptor.getValue();
        assertTrue(content.contains("John"));
        assertTrue(content.contains("123456"));
    }
}