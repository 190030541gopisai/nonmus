package com.nonmus.nonmus.modules.auth.service;

import com.nonmus.nonmus.modules.auth.dto.internal.SignUpResult;
import com.nonmus.nonmus.modules.auth.dto.internal.TokenPair;
import com.nonmus.nonmus.modules.auth.dto.request.SignUpRequest;
import com.nonmus.nonmus.modules.auth.events.EmailVerificationEvent;
import com.nonmus.nonmus.modules.common.exception.UserAlreadyExistsException;
import com.nonmus.nonmus.modules.user.dto.request.UserCreateRequest;
import com.nonmus.nonmus.modules.user.entity.Users;
import com.nonmus.nonmus.modules.user.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {
    private final UsersService usersService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ModelMapper modelMapper;
    private final TokenService tokenService;

    public SignUpResult signup(SignUpRequest request) {
        String email = request.getEmail();

        if(usersService.existsByEmail(email)) {
            throw new UserAlreadyExistsException("User already exists");
        }

        Users user = usersService.createUser(modelMapper.map(request, UserCreateRequest.class));
        applicationEventPublisher.publishEvent(new EmailVerificationEvent(user));

        return SignUpResult.builder()
                .user(user)
                .tokenPair(tokenService.generateJwtTokens(user))
                .build();
    }
}
