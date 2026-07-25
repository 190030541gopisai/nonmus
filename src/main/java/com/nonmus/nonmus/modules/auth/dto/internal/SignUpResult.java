package com.nonmus.nonmus.modules.auth.dto.internal;

import com.nonmus.nonmus.modules.user.entity.Users;
import lombok.*;
import org.antlr.v4.runtime.Token;

@Getter
@RequiredArgsConstructor
@Builder
public class SignUpResult {
    private final Users user;
    private final TokenPair tokenPair;
}
