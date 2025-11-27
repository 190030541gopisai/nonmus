package com.nonmus.user_service.service;

import org.springframework.stereotype.Service;
import com.nonmus.user_service.dto.UserRequest;
import com.nonmus.user_service.entity.User;
import com.nonmus.user_service.exception.UserAlreadyExistsException;
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
}
