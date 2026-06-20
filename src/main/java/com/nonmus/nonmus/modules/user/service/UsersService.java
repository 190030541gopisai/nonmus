package com.nonmus.nonmus.modules.user.service;

import java.util.UUID;

import com.nonmus.nonmus.modules.common.exception.UserNotFoundException;
import com.nonmus.nonmus.modules.common.storage.StorageService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.nonmus.nonmus.modules.user.dto.request.UserCreateRequest;
import com.nonmus.nonmus.modules.user.entity.Users;
import com.nonmus.nonmus.modules.user.repository.UsersRepository;


@Service
public class UsersService {
    private final UsersRepository usersRepository;
    private final StorageService storageService;


    public UsersService(UsersRepository usersRepository, StorageService storageService) {
        this.usersRepository = usersRepository;
        this.storageService = storageService;
    }

    public Users createUser(UserCreateRequest request) {
        Users user = new Users();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
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
