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

        Provider localProvider = Provider.LOCAL;

        user.setProvider(localProvider);
        user.setProfilePictureProvider(localProvider);
        return usersRepository.save(user);
    }

    public Users createOAuthUser(OAuthUserCreateRequest request) {
        Users user = new Users();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setProfilePicture(request.getExternalProfilePictureUrl());
        user.setProvider(request.getProvider());
        user.setProfilePictureProvider(request.getProvider());
        return usersRepository.save(user);
    }

    public Optional<Users> getUsersByEmailAndProvider(String email, Provider provider) {
        return usersRepository.findByEmailAndProvider(email, provider);
    }

    public boolean existsByEmailAndProvider(String email, Provider provider) {
        return usersRepository.existsByEmailAndProvider(email, provider);
    }
}
