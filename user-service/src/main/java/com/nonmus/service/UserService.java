package com.nonmus.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.nonmus.dto.UserCreateRequest;
import com.nonmus.dto.UserValidateRequest;
import com.nonmus.dto.UserValidateResponse;
import com.nonmus.entity.User;
import com.nonmus.exception.UserAlreadyExistsException;
import com.nonmus.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(UserCreateRequest request) {
        String email = request.getEmail();
        if(isUserAlreadyExists(email)) {
            throw new UserAlreadyExistsException("User Already Exists");
        }

        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        return userRepository.save(user);
    }

    private boolean isUserAlreadyExists(String email) {
        boolean userExists = userRepository.existsByEmail(email);

        if(userExists) {
            return true;
        }

        return false;
    }

    public UserValidateResponse validate(UserValidateRequest request) {
        UUID userId = request.getUserId();
        String email = request.getEmail();

        UserValidateResponse response = new UserValidateResponse();
        response.setUserIdExist(userRepository.existsById(userId));
        response.setEmailExist(userRepository.existsByEmail(email));
        response.setBelongsToSameUser(userRepository.existsByUserIdAndEmail(userId, email));

        return response;
    }

    public User getUserData(UUID userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public User getUserData(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User updateEmailVerified(UUID userId) {
        User user = getUserData(userId);
        if(user != null) {
            user.setEmailVerified(true);
            userRepository.save(user);
        }
        return user;
    }
}
