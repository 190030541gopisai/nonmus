package com.nonmus.nonmus.modules.common.util;

import com.nonmus.nonmus.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUtil {
    public static AuthenticatedUser getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (AuthenticatedUser) authentication.getPrincipal();
    }

    public static String getAuthenticatedUserEmail() {
        AuthenticatedUser authUser = AuthUtil.getPrincipal();
        if(authUser == null) {
            return null;
        }
        return authUser.getEmail();
    }
}
