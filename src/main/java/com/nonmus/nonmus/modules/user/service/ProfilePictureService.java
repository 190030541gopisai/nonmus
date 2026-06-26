package com.nonmus.nonmus.modules.user.service;

import com.nonmus.nonmus.modules.user.events.S3DeleteProfilePictureEvent;
import com.nonmus.nonmus.modules.common.storage.StorageService;
import com.nonmus.nonmus.modules.user.entity.Users;
import com.nonmus.nonmus.modules.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfilePictureService {
    private final String PROFILE_PICTURE_BUCKET = "nonmus-profile-pics";

    private final UsersRepository usersRepository;
    private final StorageService storageService;
    private final ApplicationEventPublisher publisher;

    public String upload(MultipartFile file, String email) {
        Users user = usersRepository.findByEmail(email).orElseThrow();

        String s3Key = storageService.uploadFile(PROFILE_PICTURE_BUCKET, file, "users/" + email);

        user.setProfilePicture(s3Key);
        usersRepository.save(user);

        return storageService.generatePublicUrl(s3Key);
    }
}

