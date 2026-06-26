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

    public AuthService(UsersService usersService, JwtUtil jwtUtil) {
        this.usersService = usersService;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse signup(SignUpRequest request) {
        if(userAlreadyExists(request.getEmail())) {
            throw new UserAlreadyExistsException("User already exists");
        }

        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setName(request.getName());
        userCreateRequest.setEmail(request.getEmail());
        userCreateRequest.setPassword(request.getPassword());

        Users user = usersService.createUser(userCreateRequest);
        
        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getName());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        
        return new AuthResponse(accessToken, refreshToken, "User created successfully", 
                                user.getEmail(), user.getName());
    }

    public AuthResponse login(String email, String password) {
        var user = usersService.getUserByEmail(email);
        if(!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid credentials");
        }

        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getName());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        
        return new AuthResponse(accessToken, refreshToken, "Login successful", 
                                user.getEmail(), user.getName());
    }

    private boolean userAlreadyExists(String email) {
        try {
            usersService.getUserByEmail(email);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }
}
