package com.nonmus.nonmus.modules.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nonmus.nonmus.modules.auth.dto.request.SignUpRequest;
import com.nonmus.nonmus.modules.auth.dto.response.AuthResponse;
import com.nonmus.nonmus.modules.common.exception.UserAlreadyExistsException;
import com.nonmus.nonmus.modules.common.util.JwtUtil;
import com.nonmus.nonmus.modules.user.dto.request.UserCreateRequest;
import com.nonmus.nonmus.modules.user.entity.Users;
import com.nonmus.nonmus.modules.user.service.UsersService;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsersService usersService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse signup(SignUpRequest request) {
        if(userAlreadyExists(request.getEmail())) {
            throw new UserAlreadyExistsException("User already exists");
        }

        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setName(request.getName());
        userCreateRequest.setEmail(request.getEmail());
        userCreateRequest.setPassword(request.getPassword());

        Users user = usersService.createUser(userCreateRequest);
        return generateAuthResponse(user, "User created successfully");
    }

    public AuthResponse login(String email, String password) {
        Users user = usersService.getUserByEmail(email);

        if(user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return generateAuthResponse(user, "Login successful");
    }

    private boolean userAlreadyExists(String email) {
        try {
            Users user = usersService.getUserByEmail(email);
            return user != null;
        } catch (RuntimeException e) {
            return false;
        }
    }

    private AuthResponse generateAuthResponse(Users user, String message) {
        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getName());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        return new AuthResponse(
                accessToken,
                refreshToken,
                message,
                user.getEmail(),
                user.getName()
        );
    }
}
