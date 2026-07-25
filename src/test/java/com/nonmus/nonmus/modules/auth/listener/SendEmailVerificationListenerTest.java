package com.nonmus.nonmus.modules.auth.listener;

import com.nonmus.nonmus.modules.auth.events.EmailVerificationEvent;
import com.nonmus.nonmus.modules.auth.service.MailService;
import com.nonmus.nonmus.modules.auth.service.VerificationTokenService;
import com.nonmus.nonmus.modules.user.entity.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendEmailVerificationListenerTest {

    @Mock
    private MailService mailService;

    @Mock
    private VerificationTokenService tokenService;

    @InjectMocks
    private SendEmailVerificationListener listener;

    @Captor
    private ArgumentCaptor<String> stringCaptor;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(listener, "emailVerificationLink", "https://example.com/verify?token=");
    }

    @Test
    void sendEmailVerificationLinkWithToken_shouldSendEmailWithLink() {
        Users user = new Users();
        user.setEmail("john@test.com");

        when(tokenService.createToken(user)).thenReturn("test-token");

        EmailVerificationEvent event = new EmailVerificationEvent(user);

        listener.sendEmailVerificationLinkWithToken(event);

        verify(tokenService).createToken(user);
        verify(mailService).send(
                eq("john@test.com"),
                eq("Verify Your Email Address"),
                stringCaptor.capture());

        String content = stringCaptor.getValue();
        assertTrue(content.contains("john@test.com"));
        assertTrue(content.contains("https://example.com/verify?token=test-token"));
    }
}