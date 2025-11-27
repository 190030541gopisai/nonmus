package com.nonmus.user_service.service;

import org.springframework.stereotype.Service;
import com.nonmus.user_service.dto.UserRequest;
import com.nonmus.user_service.entity.User;
import com.nonmus.user_service.exception.EmailAlreadyVerifiedException;
import com.nonmus.user_service.exception.UserAlreadyExistsException;
import com.nonmus.user_service.exception.UserNotFoundException;
import com.nonmus.user_service.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public User createUserProfile(UserRequest userRequest) {
        String email = userRequest.getEmail();

        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("User with email " + email + " already exists. Please login instead.");
        }

        User user = new User();
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword());
        user.setEmailVerified(false);

        return userRepository.save(user);
    }

    @Transactional
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Transactional
    public void activateUserEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));

        if (user.isEmailVerified()) {
            throw new EmailAlreadyVerifiedException("Email is already verified");
        }

        user.setEmailVerified(true);
        userRepository.save(user);
    }
}
