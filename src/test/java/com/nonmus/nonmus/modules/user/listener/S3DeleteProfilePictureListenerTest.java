package com.nonmus.nonmus.modules.user.listener;

import com.nonmus.nonmus.modules.common.storage.StorageService;
import com.nonmus.nonmus.modules.user.events.S3DeleteProfilePictureEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3DeleteProfilePictureListenerTest {

    @Mock
    private StorageService storageService;

    @InjectMocks
    private S3DeleteProfilePictureListener listener;

    @Test
    void deleteProfilePicture_shouldDeleteFileFromProfilePicsBucket() {
        S3DeleteProfilePictureEvent event = new S3DeleteProfilePictureEvent("users/user@test.com/pic.jpg");

        listener.deleteProfilePicture(event);

        verify(storageService).deleteFile("nonmus-profile-pics", "users/user@test.com/pic.jpg");
    }
}