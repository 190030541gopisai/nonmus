package com.nonmus.nonmus.security;

import com.nonmus.nonmus.modules.auth.dto.internal.OAuthSignUpResult;
import com.nonmus.nonmus.modules.auth.service.JwtCookieService;
import com.nonmus.nonmus.modules.auth.service.OAuthAuthenticationService;
import com.nonmus.nonmus.modules.auth.service.TokenService;
import com.nonmus.nonmus.modules.user.enums.Provider;
import com.nonmus.nonmus.modules.user.service.UsersService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final OAuthAuthenticationService oAuthAuthenticationService;
    private final TokenService tokenService;
    private final JwtCookieService jwtCookieService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User user = (OAuth2User) authentication.getPrincipal();

        if(user == null) {
            response.sendRedirect("http://localhost:5173/login");
            return;
        }

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        Provider provider = Provider.valueOf(token.getAuthorizedClientRegistrationId().toUpperCase());

        OAuthSignUpResult result = oAuthAuthenticationService.oauthSingup(user, provider, response);

        boolean rememberMe = true;
        jwtCookieService.addJwtCookies(
                tokenService.generateJwtTokens(result.getUser(), rememberMe),
                response
        );

        response.sendRedirect("http://localhost:5173");
    }
}
