package com.nonmus.nonmus.modules.auth.service;

import com.nonmus.nonmus.modules.auth.dto.internal.OAuthSignUpResult;
import com.nonmus.nonmus.modules.user.dto.request.OAuthUserCreateRequest;
import com.nonmus.nonmus.modules.user.entity.Users;
import com.nonmus.nonmus.modules.user.enums.Provider;
import com.nonmus.nonmus.modules.user.service.UsersService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuthAuthenticationServiceTest    {

    @Mock
    private UsersService usersService;

    @InjectMocks
    private OAuthAuthenticationService oAuthAuthenticationService;

    @Captor
    private ArgumentCaptor<OAuthUserCreateRequest> createRequestCaptor;

    @Test
    void oauthSingup_shouldReturnExistingUser_whenEmailExists() {
        OAuth2User oauthUser = mock(OAuth2User.class);
        when(oauthUser.getAttribute("email")).thenReturn("existing@test.com");

        Users existingUser = new Users();
        existingUser.setEmail("existing@test.com");

        when(usersService.existsByEmail("existing@test.com")).thenReturn(true);
        when(usersService.getUsersByEmail("existing@test.com")).thenReturn(Optional.of(existingUser));

        OAuthSignUpResult result = oAuthAuthenticationService.oauthSingup(oauthUser, Provider.GOOGLE, null);

        assertSame(existingUser, result.getUser());
        assertTrue(existingUser.getEmailVerified());
        verify(usersService).addProviderToUserAndSave(existingUser, Provider.GOOGLE);
        verify(usersService, never()).createOAuthUser(any());
    }

    @Test
    void oauthSingup_shouldCreateNewUser_whenEmailDoesNotExist() {
        OAuth2User oauthUser = mock(OAuth2User.class);
        when(oauthUser.getAttribute("email")).thenReturn("new@test.com");
        when(oauthUser.getAttribute("name")).thenReturn("New User");
        when(oauthUser.getAttribute("picture")).thenReturn("https://example.com/avatar.png");
        when(oauthUser.getAttribute("email_verified")).thenReturn(true);

        when(usersService.existsByEmail("new@test.com")).thenReturn(false);

        Users newUser = new Users();
        newUser.setEmail("new@test.com");
        when(usersService.createOAuthUser(any())).thenReturn(newUser);

        OAuthSignUpResult result = oAuthAuthenticationService.oauthSingup(oauthUser, Provider.GOOGLE, null);

        assertSame(newUser, result.getUser());
        verify(usersService).createOAuthUser(createRequestCaptor.capture());

        OAuthUserCreateRequest request = createRequestCaptor.getValue();
        assertEquals("New User", request.getName());
        assertEquals("new@test.com", request.getEmail());
        assertEquals("https://example.com/avatar.png", request.getExternalProfilePictureUrl());
        assertTrue(request.isEmailVerified());
        assertEquals(Provider.GOOGLE, request.getProvider());
    }
}