package com.nonmus.nonmus.modules.user.service;

import com.nonmus.nonmus.modules.common.exception.UserNotFoundException;
import com.nonmus.nonmus.modules.user.dto.request.OAuthUserCreateRequest;
import com.nonmus.nonmus.modules.user.dto.request.UpdateUserRequest;
import com.nonmus.nonmus.modules.user.dto.request.UserCreateRequest;
import com.nonmus.nonmus.modules.user.dto.response.UserResponse;
import com.nonmus.nonmus.modules.user.entity.Users;
import com.nonmus.nonmus.modules.user.enums.Provider;
import com.nonmus.nonmus.modules.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public Users createUser(UserCreateRequest request) {
        Users user = modelMapper.map(request, Users.class);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Provider localProvider = Provider.LOCAL;
        user.addProvider(localProvider);
        user.setProfilePictureProvider(localProvider);

        return usersRepository.save(user);
    }

    public Users createOAuthUser(OAuthUserCreateRequest request) {
        Users user = new Users();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setProfilePicture(request.getExternalProfilePictureUrl());

        Provider oauthProvider = request.getProvider();
        user.addProvider(oauthProvider);
        user.setProfilePictureProvider(oauthProvider);

        user.setEmailVerified(request.isEmailVerified());
        return usersRepository.save(user);
    }

    public Optional<Users> getUsersByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return usersRepository.existsByEmail(email);
    }

    @Transactional
    public void addProviderToUserAndSave(Users user, Provider externalProvider) {
        user.addProvider(externalProvider);
        usersRepository.save(user);
    }

    @Transactional
    public Users updateUser(String email, UpdateUserRequest request) {
        Users user = usersRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
        modelMapper.map(request, user);
        return usersRepository.save(user);
    }
}
