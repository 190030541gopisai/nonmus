package com.nonmus.nonmus.modules.auth.service;

import com.nonmus.nonmus.modules.auth.dto.internal.OAuthSignUpResult;
import com.nonmus.nonmus.modules.user.dto.request.OAuthUserCreateRequest;
import com.nonmus.nonmus.modules.user.entity.Users;
import com.nonmus.nonmus.modules.user.enums.Provider;
import com.nonmus.nonmus.modules.user.service.UsersService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OAuthAuthenticationService {
    private final UsersService usersService;

    @Transactional
    public OAuthSignUpResult oauthSingup(OAuth2User oauthUser, Provider provider, HttpServletResponse response){
        String email = oauthUser.getAttribute("email");

        if(usersService.existsByEmail(email)) {
            Users user = usersService.getUsersByEmail(email).get();
            user.setEmailVerified(true);

            usersService.addProviderToUserAndSave(user, provider);

            return OAuthSignUpResult.builder()
                    .user(user)
                    .build();
        }

        OAuthUserCreateRequest request = new OAuthUserCreateRequest();
        request.setName(oauthUser.getAttribute("name"));
        request.setEmail(oauthUser.getAttribute("email"));
        request.setExternalProfilePictureUrl(oauthUser.getAttribute("picture"));
        request.setEmailVerified(Boolean.TRUE.equals(oauthUser.getAttribute("email_verified")));
        request.setProvider(provider);

        Users user = usersService.createOAuthUser(request);

        return OAuthSignUpResult.builder()
                .user(user)
                .build();
    }
}
