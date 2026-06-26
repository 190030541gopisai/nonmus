package com.nonmus.nonmus.modules.user.service;

import java.util.UUID;

import com.nonmus.nonmus.modules.common.exception.UserNotFoundException;
import com.nonmus.nonmus.modules.common.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nonmus.nonmus.modules.user.dto.request.UserCreateRequest;
import com.nonmus.nonmus.modules.user.entity.Users;
import com.nonmus.nonmus.modules.user.repository.UsersRepository;


@Service
@RequiredArgsConstructor
public class UsersService {
    private final UsersRepository usersRepository;
    private final StorageService storageService;
    private final PasswordEncoder passwordEncoder;

    public Users createUser(UserCreateRequest request) {
        Users user = new Users();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return usersRepository.save(user);
    }

    public Users getUserByEmail(String email) {
        Users user = usersRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
        String key = user.getProfilePicture();
        String cdnUrl = storageService.generatePublicUrl(key);
        user.setProfilePicture(cdnUrl);
        return user;
    }
}
