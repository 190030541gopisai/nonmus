package com.nonmus.nonmus.modules.auth.dto.internal;

import com.nonmus.nonmus.modules.user.entity.Users;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class LoginResult {
    private final Users user;
    private final TokenPair tokenPair;
}
