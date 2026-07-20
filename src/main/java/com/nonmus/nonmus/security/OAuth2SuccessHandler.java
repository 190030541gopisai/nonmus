package com.nonmus.nonmus.security;

import com.nonmus.nonmus.modules.auth.service.AuthenticationService;
import com.nonmus.nonmus.modules.user.enums.Provider;
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
    private final AuthenticationService authenticationService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User user = (OAuth2User) authentication.getPrincipal();
        if(user == null) {
            response.sendRedirect("http://localhost:5173/login");
        }
        OAuth2AuthenticationToken token =
                (OAuth2AuthenticationToken) authentication;
        Provider provider = Provider.valueOf(token.getAuthorizedClientRegistrationId().toUpperCase());
        authenticationService.oauthSingIn(user, provider, response);
        response.sendRedirect("http://localhost:5173");
    }
}
