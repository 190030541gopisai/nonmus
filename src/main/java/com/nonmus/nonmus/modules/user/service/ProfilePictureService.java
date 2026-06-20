package com.nonmus.nonmus.modules.user.service;

import com.nonmus.nonmus.modules.common.storage.StorageService;
import com.nonmus.nonmus.modules.user.entity.Users;
import com.nonmus.nonmus.modules.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProfilePictureService {
    private final UsersRepository usersRepository;
    private final StorageService storageService;

    private final String PROFILE_PICTURE_BUCKET = "nonmus-profile-pics";

    public String upload(MultipartFile file, String email) {
        Users user = usersRepository.findByEmail(email).orElseThrow();

        String s3Key = storageService.uploadFile(PROFILE_PICTURE_BUCKET, file, "users/" + email);

        user.setProfilePicture(s3Key);
        usersRepository.save(user);

        return storageService.generatePublicUrl(s3Key);
    }
}

