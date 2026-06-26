package com.nonmus.nonmus.modules.user.controller;

import com.nonmus.nonmus.modules.common.exception.UserNotFoundException;
import com.nonmus.nonmus.modules.common.util.AuthUtil;
import com.nonmus.nonmus.modules.user.entity.Users;
import com.nonmus.nonmus.modules.user.repository.UsersRepository;
import com.nonmus.nonmus.modules.user.service.ProfilePictureService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class ProfilePictureController {
    private final ProfilePictureService profilePictureService;

    @PutMapping("/profile-picture")
    public String uploadProfilePicture(@RequestParam("profilePicture") MultipartFile profilePicture) {
        String email = AuthUtil.getPrincipal();
        return profilePictureService.upload(profilePicture, email);
    }
}
