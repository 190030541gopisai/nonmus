package com.nonmus.nonmus.modules.user.service;

import com.nonmus.nonmus.modules.user.dto.request.OAuthUserCreateRequest;
import com.nonmus.nonmus.modules.user.dto.request.UserCreateRequest;
import com.nonmus.nonmus.modules.user.entity.Users;
import com.nonmus.nonmus.modules.user.enums.Provider;
import com.nonmus.nonmus.modules.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public Users createUser(UserCreateRequest request) {
        Users user = new Users();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setProfilePicture("users/default-avatar.png");
        user.setProvider(Provider.LOCAL);
        return usersRepository.save(user);
    }

    public Users createOAuthUser(OAuthUserCreateRequest request) {
        Users user = new Users();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setProfilePicture(request.getExternalProfilePictureUrl());
        user.setProvider(request.getProvider());
        return usersRepository.save(user);
    }

    /**
     * Returns the user entity with the raw S3 key in profilePicture.
     * To get a renderable URL for the profile picture, call
     * ProfilePictureService.getViewUrl(email) — it generates a fresh presigned GET URL.
     */
    public Optional<Users> getUserByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return usersRepository.existsByEmail(email);
    }
}
