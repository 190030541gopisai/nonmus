package com.nonmus.nonmus.modules.auth.service;

import com.nonmus.nonmus.modules.common.exception.UserNotFoundException;
import com.nonmus.nonmus.modules.common.util.JwtUtil;
import com.nonmus.nonmus.modules.user.entity.Users;
import com.nonmus.nonmus.modules.user.service.UsersService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private UsersService usersService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private TokenService tokenService;

    @Mock
    private JwtCookieService jwtCookieService;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    void refreshAccessToken_shouldAddNewAccessTokenCookie_whenRefreshTokenIsValid() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        Cookie refreshCookie = new Cookie("refresh_token", "valid-refresh-token");
        when(request.getCookies()).thenReturn(new Cookie[]{refreshCookie});
        when(jwtUtil.isTokenValid("valid-refresh-token")).thenReturn(true);
        when(jwtUtil.getEmailFromToken("valid-refresh-token")).thenReturn("user@test.com");

        Users user = new Users();
        user.setEmail("user@test.com");
        when(usersService.getUsersByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(tokenService.generateAccessToken(user)).thenReturn("new-access-token");

        refreshTokenService.refreshAccessToken(request, response);

        verify(jwtCookieService).addAccessTokenCookieToResponse("new-access-token", response);
    }

    @Test
    void refreshAccessToken_shouldDoNothing_whenNoRefreshTokenCookie() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getCookies()).thenReturn(new Cookie[]{});

        refreshTokenService.refreshAccessToken(request, response);

        verifyNoInteractions(jwtUtil, usersService, tokenService, jwtCookieService);
    }

    @Test
    void refreshAccessToken_shouldDoNothing_whenCookiesAreNull() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getCookies()).thenReturn(null);

        refreshTokenService.refreshAccessToken(request, response);

        verifyNoInteractions(jwtUtil, usersService, tokenService, jwtCookieService);
    }

    @Test
    void refreshAccessToken_shouldDoNothing_whenTokenIsInvalid() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        Cookie refreshCookie = new Cookie("refresh_token", "invalid-token");
        when(request.getCookies()).thenReturn(new Cookie[]{refreshCookie});
        when(jwtUtil.isTokenValid("invalid-token")).thenReturn(false);

        refreshTokenService.refreshAccessToken(request, response);

        verify(jwtUtil, never()).getEmailFromToken(any());
        verifyNoInteractions(usersService, tokenService, jwtCookieService);
    }

    @Test
    void refreshAccessToken_shouldThrowUserNotFound_whenUserDoesNotExist() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        Cookie refreshCookie = new Cookie("refresh_token", "valid-token");
        when(request.getCookies()).thenReturn(new Cookie[]{refreshCookie});
        when(jwtUtil.isTokenValid("valid-token")).thenReturn(true);
        when(jwtUtil.getEmailFromToken("valid-token")).thenReturn("unknown@test.com");
        when(usersService.getUsersByEmail("unknown@test.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> refreshTokenService.refreshAccessToken(request, response));
    }
}