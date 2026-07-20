package com.nonmus.nonmus.modules.user.dto.request;

import com.nonmus.nonmus.modules.user.enums.Provider;
import lombok.Data;

@Data
public class OAuthUserCreateRequest {
    private String name;
    private String email;
    private String externalProfilePictureUrl;
    private Provider provider;
    private boolean emailVerified;
}
