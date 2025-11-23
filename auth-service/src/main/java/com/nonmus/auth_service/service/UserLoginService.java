package com.nonmus.auth_service.service;

import com.nonmus.auth_service.dto.LoginRequest;
import com.nonmus.auth_service.dto.SendEmailOtpRequest;
import com.nonmus.auth_service.entity.User;
import com.nonmus.auth_service.exception.EmailNotVerifiedException;
import com.nonmus.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserLoginService {

    private final AuthenticationManager authenticationManager;
    private final EmailOtpService emailOtpService;
    private final UserRepository userRepository;

    public User authenticate(LoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();
        log.info("Authenticating user with email: " + email);

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (AuthenticationException ex) {
            log.info("Invalid credentials for email: " + email);
            throw new BadCredentialsException("Invalid email or password.");
        }

        // After successful authentication, fetch the user entity from the database.
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password."));

        if (Boolean.FALSE.equals(user.getIsEmailVerified())) {
            log.info("Email not verified for user with email: " + email + ". Login attempt denied. Resending OTP.");

            // Resend OTP
            SendEmailOtpRequest sendEmailOtpRequest = new SendEmailOtpRequest();
            sendEmailOtpRequest.setEmail(email);
            emailOtpService.sendEmailOtp(sendEmailOtpRequest);

            throw new EmailNotVerifiedException("User with email " + email + " is Not Verified. A new OTP has been sent. Please verify!");
        }

        return user;
    }
}
