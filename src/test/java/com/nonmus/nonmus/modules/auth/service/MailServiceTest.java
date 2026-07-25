package com.nonmus.nonmus.modules.auth.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private MailService mailService;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> messageCaptor;

    @Test
    void send_shouldCreateAndSendMessage() {
        mailService.send("to@test.com", "Subject", "Content body");

        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();
        assertArrayEquals(new String[]{"to@test.com"}, message.getTo());
        assertEquals("Subject", message.getSubject());
        assertEquals("Content body", message.getText());
    }
}