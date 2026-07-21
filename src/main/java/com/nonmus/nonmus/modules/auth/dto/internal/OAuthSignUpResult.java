package com.nonmus.nonmus.modules.auth.dto.internal;

import com.nonmus.nonmus.modules.user.entity.Users;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OAuthSignUpResult {
    private Users user;
}
