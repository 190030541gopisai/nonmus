package com.nonmus.nonmus.modules.user.service;

import com.nonmus.nonmus.modules.user.dto.request.OAuthUserCreateRequest;
import com.nonmus.nonmus.modules.user.dto.request.UserCreateRequest;
import com.nonmus.nonmus.modules.user.entity.Providers;
import com.nonmus.nonmus.modules.user.entity.Users;
import com.nonmus.nonmus.modules.user.enums.Provider;
import com.nonmus.nonmus.modules.user.repository.ProvidersRepository;
import com.nonmus.nonmus.modules.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;
    private final ProvidersRepository providersRepository;
    private final PasswordEncoder passwordEncoder;

    public Users createUser(UserCreateRequest request) {

        Users user = new Users();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setProfilePicture("users/default-avatar.png");

        Provider localProvider = Provider.LOCAL;

        Providers provider = new Providers();
        provider.setUser(user);
        provider.setProvider(localProvider);

        user.setProfilePictureProvider(localProvider);

        usersRepository.save(user);
        providersRepository.save(provider);

        return user;
    }

    public Users createOAuthUser(OAuthUserCreateRequest request) {
        Users user = new Users();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setProfilePicture(request.getExternalProfilePictureUrl());

        Providers provider = new Providers();
        provider.setUser(user);
        provider.setProvider(request.getProvider());

        user.setProfilePictureProvider(request.getProvider());
        user.setEmailVerified(request.isEmailVerified());
        return usersRepository.save(user);
    }

    public Optional<Users> getUsersByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return usersRepository.existsByEmail(email);
    }

    public void updateUserAndProvider(Users user, Provider externalProvider) {
        Providers provider = new Providers();
        provider.setUser(user);
        provider.setProvider(externalProvider);

        providersRepository.save(provider);
        usersRepository.save(user);
    }
}
