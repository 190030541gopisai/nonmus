package com.nonmus.nonmus.modules.user.dto.request;

import lombok.Data;

@Data
public class OAuthUserCreateRequest {
    private String name;
    private String email;
    private String externalProfilePictureUrl;
}
