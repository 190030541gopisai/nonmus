package com.nonmus.nonmus.modules.auth.service;

import com.nonmus.nonmus.modules.auth.util.AuthResponseGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nonmus.nonmus.modules.auth.dto.response.AuthResponse;
import com.nonmus.nonmus.modules.user.entity.Users;
import com.nonmus.nonmus.modules.user.service.UsersService;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UsersService usersService;
    private final PasswordEncoder passwordEncoder;
    private final AuthResponseGenerator authResponseGenerator;

    public AuthResponse login(String email, String password) {
        Users user = usersService.getUserByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return authResponseGenerator.generateAuthResponse(user, "Login successful");
    }
}
