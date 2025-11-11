package com.nonmus.auth_service.service;

import com.nonmus.auth_service.dto.RegistrationRequest;
import com.nonmus.auth_service.dto.SendEmailOtpRequest;
import com.nonmus.auth_service.entity.User;
import com.nonmus.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final EmailOtpService emailOtpService;

    public void register(RegistrationRequest request) {
        String email = request.getEmail();

        if(userRepository.findByEmail(email).isPresent()) {
            return;
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(email);
        user.setPassword(request.getPassword());

        userRepository.save(user);

        SendEmailOtpRequest sendEmailOtpRequest = new SendEmailOtpRequest();
        sendEmailOtpRequest.setEmail(email);

        emailOtpService.sendEmailOtp(sendEmailOtpRequest);
    }
}
