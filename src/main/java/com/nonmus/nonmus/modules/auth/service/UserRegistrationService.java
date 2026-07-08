package com.nonmus.nonmus.modules.auth.service;

import com.nonmus.nonmus.modules.auth.dto.request.SignUpRequest;
import com.nonmus.nonmus.modules.auth.dto.response.AuthResponse;
import com.nonmus.nonmus.modules.auth.util.AuthResponseGenerator;
import com.nonmus.nonmus.modules.common.exception.UserAlreadyExistsException;
import com.nonmus.nonmus.modules.common.util.JwtUtil;
import com.nonmus.nonmus.modules.user.dto.request.UserCreateRequest;
import com.nonmus.nonmus.modules.user.entity.Users;
import com.nonmus.nonmus.modules.user.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {
    private final UsersService usersService;
    private final PasswordEncoder passwordEncoder;
    private final AuthResponseGenerator authResponseGenerator;

    public AuthResponse signup(SignUpRequest request) {
        if(usersService.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User already exists");
        }

        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setName(request.getName());
        userCreateRequest.setEmail(request.getEmail());
        userCreateRequest.setPassword(request.getPassword());

        Users user = usersService.createUser(userCreateRequest);
        return authResponseGenerator.generateAuthResponse(user, "User created successfully");
    }
}
