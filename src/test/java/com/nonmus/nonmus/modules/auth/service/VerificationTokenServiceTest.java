package com.nonmus.nonmus.modules.auth.service;

import com.nonmus.nonmus.modules.auth.entity.EmailVerificationToken;
import com.nonmus.nonmus.modules.auth.events.EmailVerificationEvent;
import com.nonmus.nonmus.modules.auth.repository.EmailVerificationTokenRepository;
import com.nonmus.nonmus.modules.auth.util.VerificationTokenGenerator;
import com.nonmus.nonmus.modules.common.exception.EmailAlreadyVerifiedException;
import com.nonmus.nonmus.modules.common.exception.InvalidVerificationTokenException;
import com.nonmus.nonmus.modules.common.exception.UserNotFoundException;
import com.nonmus.nonmus.modules.common.exception.VerificationTokenExpiredException;
import com.nonmus.nonmus.modules.user.entity.Users;
import com.nonmus.nonmus.modules.user.repository.UsersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerificationTokenServiceTest {

    @Mock
    private EmailVerificationTokenRepository tokenRepository;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private ApplicationEventPublisher publisher;

    @Mock
    private VerificationTokenGenerator generator;

    @InjectMocks
    private VerificationTokenService verificationTokenService;

    @Captor
    private ArgumentCaptor<EmailVerificationEvent> eventCaptor;

    @Test
    void createToken_shouldCreateNewToken_whenNoExistingTokenForUser() {
        Users user = new Users();
        user.setEmail("test@test.com");

        when(generator.generate()).thenReturn("generated-token");
        when(tokenRepository.findByUser(user)).thenReturn(Optional.empty());

        String token = verificationTokenService.createToken(user);

        assertEquals("generated-token", token);
        verify(tokenRepository).save(argThat(t ->
                t.getToken().equals("generated-token") &&
                t.getUser() == user &&
                !t.isUsed()));
    }

    @Test
    void createToken_shouldUpdateExistingToken_whenTokenAlreadyExistsForUser() {
        Users user = new Users();
        user.setEmail("test@test.com");

        EmailVerificationToken existing = new EmailVerificationToken();
        existing.setToken("old-token");

        when(generator.generate()).thenReturn("new-token");
        when(tokenRepository.findByUser(user)).thenReturn(Optional.of(existing));

        String token = verificationTokenService.createToken(user);

        assertEquals("new-token", token);
        verify(tokenRepository).save(existing);
        assertEquals("new-token", existing.getToken());
        assertFalse(existing.isUsed());
    }

    @Test
    void verify_shouldMarkVerified_whenTokenIsValid() {
        Users user = new Users();
        user.setEmail("test@test.com");

        EmailVerificationToken token = new EmailVerificationToken();
        token.setToken("valid-token");
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().plusHours(1));
        token.setUsed(false);

        when(tokenRepository.findByToken("valid-token")).thenReturn(Optional.of(token));

        verificationTokenService.verify("valid-token");

        assertTrue(user.getEmailVerified());
        assertTrue(token.isUsed());
        verify(usersRepository).save(user);
        verify(tokenRepository).save(token);
    }

    @Test
    void verify_shouldThrowInvalidToken_whenTokenNotFound() {
        when(tokenRepository.findByToken("unknown")).thenReturn(Optional.empty());

        assertThrows(InvalidVerificationTokenException.class,
                () -> verificationTokenService.verify("unknown"));
    }

    @Test
    void verify_shouldThrowAlreadyVerified_whenTokenAlreadyUsed() {
        EmailVerificationToken token = new EmailVerificationToken();
        token.setUsed(true);
        token.setExpiresAt(LocalDateTime.now().plusHours(1));

        when(tokenRepository.findByToken("used-token")).thenReturn(Optional.of(token));

        assertThrows(EmailAlreadyVerifiedException.class,
                () -> verificationTokenService.verify("used-token"));
    }

    @Test
    void verify_shouldThrowExpired_whenTokenExpired() {
        EmailVerificationToken token = new EmailVerificationToken();
        token.setUsed(false);
        token.setExpiresAt(LocalDateTime.now().minusHours(1));

        when(tokenRepository.findByToken("expired-token")).thenReturn(Optional.of(token));

        assertThrows(VerificationTokenExpiredException.class,
                () -> verificationTokenService.verify("expired-token"));
    }

    @Test
    void resend_shouldPublishEvent_whenUserExistsAndNotVerified() {
        Users user = new Users();
        user.setEmail("test@test.com");
        user.setEmailVerified(false);

        when(usersRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        verificationTokenService.resend("test@test.com");

        verify(publisher).publishEvent(eventCaptor.capture());
        assertSame(user, eventCaptor.getValue().getUser());
    }

    @Test
    void resend_shouldThrowUserNotFound_whenUserDoesNotExist() {
        when(usersRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> verificationTokenService.resend("unknown@test.com"));
    }

    @Test
    void resend_shouldThrowAlreadyVerified_whenEmailAlreadyVerified() {
        Users user = new Users();
        user.setEmail("test@test.com");
        user.setEmailVerified(true);

        when(usersRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        assertThrows(EmailAlreadyVerifiedException.class,
                () -> verificationTokenService.resend("test@test.com"));
    }
}