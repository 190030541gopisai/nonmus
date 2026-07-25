package com.nonmus.nonmus.modules.auth.service;

import com.nonmus.nonmus.modules.auth.dto.internal.LoginResult;
import com.nonmus.nonmus.modules.auth.dto.internal.TokenPair;
import com.nonmus.nonmus.modules.auth.dto.request.ForgotPasswordRequest;
import com.nonmus.nonmus.modules.auth.events.EmailForgotPasswordEvent;
import com.nonmus.nonmus.modules.common.exception.UserNotFoundException;
import com.nonmus.nonmus.modules.common.util.AuthUtil;
import com.nonmus.nonmus.modules.user.entity.Users;
import com.nonmus.nonmus.modules.user.service.UsersService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UsersService usersService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthUtil authUtil;

    @Mock
    private ApplicationEventPublisher publisher;

    @Mock
    private TokenService tokenService;

    @Mock
    private JwtCookieService jwtCookieService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Captor
    private ArgumentCaptor<EmailForgotPasswordEvent> eventCaptor;

    @Test
    void login_shouldReturnLoginResult_whenCredentialsAreCorrect() {
        Users user = new Users();
        user.setEmail("test@test.com");
        user.setPassword("encodedPassword");

        TokenPair tokenPair = new TokenPair("access", "refresh");

        when(usersService.getUsersByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(tokenService.generateJwtTokens(user, false)).thenReturn(tokenPair);

        LoginResult result = authenticationService.login("test@test.com", "password", false);

        assertSame(user, result.getUser());
        assertSame(tokenPair, result.getTokenPair());
    }

    @Test
    void login_shouldThrowBadCredentials_whenUserNotFound() {
        when(usersService.getUsersByEmail("test@test.com")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class,
                () -> authenticationService.login("test@test.com", "password", false));
    }

    @Test
    void login_shouldThrowBadCredentials_whenPasswordDoesNotMatch() {
        Users user = new Users();
        user.setEmail("test@test.com");
        user.setPassword("encodedPassword");

        when(usersService.getUsersByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(false);

        assertThrows(BadCredentialsException.class,
                () -> authenticationService.login("test@test.com", "password", false));
    }

    @Test
    void login_shouldThrowBadCredentials_whenUserHasNoPassword() {
        Users user = new Users();
        user.setEmail("test@test.com");

        when(usersService.getUsersByEmail("test@test.com")).thenReturn(Optional.of(user));

        assertThrows(BadCredentialsException.class,
                () -> authenticationService.login("test@test.com", "password", false));
    }

    @Test
    void logout_shouldRemoveJwtCookies() {
        HttpServletResponse response = mock(HttpServletResponse.class);

        authenticationService.logout(response);

        verify(jwtCookieService).removeJwtCookies(response);
    }

    @Test
    void forgotPassword_shouldPublishEvent_whenUserExists() {
        Users user = new Users();
        user.setEmail("test@test.com");

        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("test@test.com");

        when(usersService.getUsersByEmail("test@test.com")).thenReturn(Optional.of(user));

        authenticationService.forgotPassword(request);

        verify(publisher).publishEvent(eventCaptor.capture());
        assertSame(user, eventCaptor.getValue().getUser());
    }

    @Test
    void forgotPassword_shouldThrowUserNotFound_whenUserDoesNotExist() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("test@test.com");

        when(usersService.getUsersByEmail("test@test.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> authenticationService.forgotPassword(request));
    }
}