package com.nonmus.auth_service.service;

import com.nonmus.auth_service.dto.RegistrationRequest;
import com.nonmus.auth_service.dto.SendEmailOtpRequest;
import com.nonmus.auth_service.entity.User;
import com.nonmus.auth_service.exception.UserAlreadyExistsException;
import com.nonmus.auth_service.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final EmailOtpService emailOtpService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void register(RegistrationRequest request) {
        String email = request.getEmail();
        log.info("Registering user with email " + email);

        if(userRepository.findByEmail(email).isPresent()) {
            log.info("User with email " + email + " already exists");
            throw new UserAlreadyExistsException("User with email " + email + " already exists. Please login instead.");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        SendEmailOtpRequest sendEmailOtpRequest = new SendEmailOtpRequest();
        sendEmailOtpRequest.setEmail(email);

        emailOtpService.sendEmailOtp(sendEmailOtpRequest);
    }
}
