package com.nonmus.nonmus.modules.user.controller;

import com.nonmus.nonmus.modules.common.exception.UserNotFoundException;
import com.nonmus.nonmus.modules.user.entity.Users;
import com.nonmus.nonmus.modules.user.repository.UsersRepository;
import com.nonmus.nonmus.modules.user.service.ProfilePictureService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class ProfilePictureController {
    private final UsersRepository usersRepository;
    private final ProfilePictureService profilePictureService;

    public ProfilePictureController(UsersRepository usersRepository, ProfilePictureService profilePictureService) {
        this.usersRepository = usersRepository;
        this.profilePictureService = profilePictureService;
    }

    @PutMapping("/profile-picture")
    public String uploadProfilePicture(@RequestParam("profilePicture") MultipartFile profilePicture) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) authentication.getPrincipal();
        return profilePictureService.upload(profilePicture, email);
    }
}
