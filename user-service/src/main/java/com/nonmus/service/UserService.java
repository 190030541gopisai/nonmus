package com.nonmus.service;

import org.springframework.stereotype.Service;

import com.nonmus.dto.UserCreateRequest;
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
        User user = userRepository.findByEmail(email).orElse(null);

        if(user != null) {
            return true;
        }

        return false;
    }
}
