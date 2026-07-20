package com.nonmus.nonmus.modules.auth.service;

import com.nonmus.nonmus.modules.auth.dto.request.SignUpRequest;
import com.nonmus.nonmus.modules.auth.dto.response.SignUpResponse;
import com.nonmus.nonmus.modules.auth.events.EmailVerificationEvent;
import com.nonmus.nonmus.modules.common.exception.UserAlreadyExistsException;
import com.nonmus.nonmus.modules.common.util.AuthUtil;
import com.nonmus.nonmus.modules.user.dto.request.UserCreateRequest;
import com.nonmus.nonmus.modules.user.entity.Users;
import com.nonmus.nonmus.modules.user.service.UsersService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {
    private final UsersService usersService;
    private final PasswordEncoder passwordEncoder;
    private final AuthUtil authUtil;
    private final ApplicationEventPublisher applicationEventPublisher;

    public SignUpResponse signup(SignUpRequest request, HttpServletResponse response) {
        String email = request.getEmail();
        if(usersService.existsByEmail(email)) {
            throw new UserAlreadyExistsException("User already exists");
        }

        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setName(request.getName());
        userCreateRequest.setEmail(email);
        userCreateRequest.setPassword(request.getPassword());

        Users user = usersService.createUser(userCreateRequest);
        authUtil.addJwtTokenCookiesToResponse(user, response);

        SignUpResponse signUpResponse = new SignUpResponse();
        signUpResponse.setMessage("Signup successful");

        EmailVerificationEvent event = new EmailVerificationEvent();
        event.setUser(user);
        applicationEventPublisher.publishEvent(event);

        return signUpResponse;
    }
}
