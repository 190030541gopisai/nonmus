package com.nonmus.nonmus.modules.auth.service;

import com.nonmus.nonmus.modules.auth.dto.internal.SignUpResult;
import com.nonmus.nonmus.modules.auth.dto.internal.TokenPair;
import com.nonmus.nonmus.modules.auth.dto.request.SignUpRequest;
import com.nonmus.nonmus.modules.auth.events.EmailVerificationEvent;
import com.nonmus.nonmus.modules.common.exception.UserAlreadyExistsException;
import com.nonmus.nonmus.modules.user.dto.request.UserCreateRequest;
import com.nonmus.nonmus.modules.user.entity.Users;
import com.nonmus.nonmus.modules.user.service.UsersService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceTest {

    @Mock
    private UsersService usersService;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private UserRegistrationService userRegistrationService;

    @Captor
    private ArgumentCaptor<EmailVerificationEvent> eventCaptor;

    @Test
    void signup_shouldCreateUserAndPublishEvent_whenEmailNotTaken() {
        SignUpRequest request = new SignUpRequest();
        request.setEmail("new@test.com");
        request.setName("New");
        request.setPassword("password");

        UserCreateRequest createRequest = new UserCreateRequest();

        when(usersService.existsByEmail("new@test.com")).thenReturn(false);
        when(modelMapper.map(request, UserCreateRequest.class)).thenReturn(createRequest);

        Users user = new Users();
        user.setEmail("new@test.com");
        when(usersService.createUser(createRequest)).thenReturn(user);

        TokenPair tokenPair = new TokenPair("access", "refresh");
        when(tokenService.generateJwtTokens(user)).thenReturn(tokenPair);

        SignUpResult result = userRegistrationService.signup(request);

        assertSame(user, result.getUser());
        assertSame(tokenPair, result.getTokenPair());
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
        assertSame(user, eventCaptor.getValue().getUser());
    }

    @Test
    void signup_shouldThrowUserAlreadyExists_whenEmailTaken() {
        SignUpRequest request = new SignUpRequest();
        request.setEmail("existing@test.com");

        when(usersService.existsByEmail("existing@test.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class,
                () -> userRegistrationService.signup(request));

        verifyNoInteractions(modelMapper, applicationEventPublisher, tokenService);
        verify(usersService, never()).createUser(any());
    }
}