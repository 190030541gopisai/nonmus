package com.nonmus.auth_service.service;

import com.nonmus.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsersDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(username == null || username.isEmpty()) {
            return null;
        }

        var userOpt = userRepository.findByEmail(username);
        if(userOpt.isPresent()) {
            return userOpt.get();
        }

        return null;
    }
}
