package com.nonmus.nonmus.modules.user.listener;


import com.nonmus.nonmus.modules.common.storage.StorageService;
import com.nonmus.nonmus.modules.user.events.S3DeleteProfilePictureEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class S3DeleteProfilePictureListener {
    private final String PROFILE_PICTURE_BUCKET = "nonmus-profile-pics";

    private final StorageService storageService;

    @EventListener
    public void deleteProfilePicture(S3DeleteProfilePictureEvent event) {
        String key = event.getKey();
        storageService.deleteFile(PROFILE_PICTURE_BUCKET, key);
    }
}