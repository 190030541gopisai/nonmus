package com.nonmus.nonmus.security;

import com.nonmus.nonmus.modules.auth.dto.internal.OAuthSignUpResult;
import com.nonmus.nonmus.modules.auth.dto.internal.TokenPair;
import com.nonmus.nonmus.modules.auth.service.JwtCookieService;
import com.nonmus.nonmus.modules.auth.service.OAuthAuthenticationService;
import com.nonmus.nonmus.modules.auth.service.TokenService;
import com.nonmus.nonmus.modules.user.entity.Users;
import com.nonmus.nonmus.modules.user.enums.Provider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2SuccessHandlerTest {

    @Mock
    private OAuthAuthenticationService oAuthAuthenticationService;

    @Mock
    private TokenService tokenService;

    @Mock
    private JwtCookieService jwtCookieService;

    @InjectMocks
    private OAuth2SuccessHandler handler;

    @Test
    void onAuthenticationSuccess_shouldRedirectToLogin_whenPrincipalIsNull() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        OAuth2AuthenticationToken authentication = mock(OAuth2AuthenticationToken.class);

        when(authentication.getPrincipal()).thenReturn(null);

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(response).sendRedirect("http://localhost:5173/login");
        verifyNoInteractions(oAuthAuthenticationService, tokenService, jwtCookieService);
    }

    @Test
    void onAuthenticationSuccess_shouldAddCookiesAndRedirectToHome() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        OAuth2AuthenticationToken authentication = mock(OAuth2AuthenticationToken.class);

        OAuth2User oauthUser = mock(OAuth2User.class);
        when(authentication.getPrincipal()).thenReturn(oauthUser);
        when(authentication.getAuthorizedClientRegistrationId()).thenReturn("google");

        Users user = new Users();
        user.setEmail("user@gmail.com");
        OAuthSignUpResult signUpResult = OAuthSignUpResult.builder().user(user).build();

        when(oAuthAuthenticationService.oauthSingup(oauthUser, Provider.GOOGLE, response))
                .thenReturn(signUpResult);

        TokenPair tokenPair = new TokenPair("access", "refresh");
        when(tokenService.generateJwtTokens(user, true)).thenReturn(tokenPair);

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(jwtCookieService).addJwtCookies(tokenPair, response);
        verify(response).sendRedirect("http://localhost:5173");
    }
}