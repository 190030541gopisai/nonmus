package com.nonmus.auth_service.service;

import com.nonmus.auth_service.dto.LoginRequest;
import com.nonmus.auth_service.entity.User;
import com.nonmus.auth_service.exception.BadCredentialsException;
import com.nonmus.auth_service.exception.EmailNotVerifiedException;
import com.nonmus.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserLoginService {

    private final UserRepository userRepository;

    public User authenticate(LoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password."));

        if(!user.getPassword().equals(password)) {
            throw new BadCredentialsException("Invalid email or password.");
        }

        if(!user.getIsEmailVerified()) {
            throw new EmailNotVerifiedException("User with email " + email + " is Not Verified. Please verify!");
        }

        return user;
    }
}
