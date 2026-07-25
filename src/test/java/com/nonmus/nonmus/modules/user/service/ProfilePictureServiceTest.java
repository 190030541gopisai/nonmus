package com.nonmus.nonmus.modules.user.service;

import com.nonmus.nonmus.config.props.StorageProperties;
import com.nonmus.nonmus.modules.common.exception.InvalidFileException;
import com.nonmus.nonmus.modules.common.storage.StorageService;
import com.nonmus.nonmus.modules.common.storage.dto.PresignedUploadResult;
import com.nonmus.nonmus.modules.user.dto.request.ConfirmUploadRequest;
import com.nonmus.nonmus.modules.user.dto.request.PresignedUrlRequest;
import com.nonmus.nonmus.modules.user.dto.response.PresignedUrlResponse;
import com.nonmus.nonmus.modules.user.dto.response.ViewUrlResponse;
import com.nonmus.nonmus.modules.user.entity.Users;
import com.nonmus.nonmus.modules.user.enums.Provider;
import com.nonmus.nonmus.modules.user.repository.UsersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfilePictureServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private StorageService storageService;

    @Mock
    private StorageProperties storageProperties;

    @InjectMocks
    private ProfilePictureService profilePictureService;

    @Captor
    private ArgumentCaptor<String> keyCaptor;

    @Test
    void generatePresignedUploadUrl_shouldReturnPresignedUrl() {
        when(storageProperties.getAllowedImageTypes()).thenReturn(List.of("image/jpeg", "image/png"));
        when(storageProperties.getMaxProfilePictureBytes()).thenReturn(5_242_880L);
        when(storageProperties.getProfilePictureBucket()).thenReturn("nonmus-profile-pics");
        when(storageProperties.getUploadExpirySeconds()).thenReturn(900);

        PresignedUrlRequest req = new PresignedUrlRequest("image/jpeg", 50000);

        when(storageService.generatePresignedUploadUrl(
                eq("nonmus-profile-pics"), anyString(), eq("image/jpeg"), eq(Duration.ofSeconds(900))))
                .thenReturn(new PresignedUploadResult("https://presigned-url", "users/test@test.com/uuid.jpg"));

        PresignedUrlResponse response = profilePictureService.generatePresignedUploadUrl(req, "test@test.com");

        assertEquals("https://presigned-url", response.presignedUrl());
        assertEquals("users/test@test.com/uuid.jpg", response.s3Key());
        assertEquals(900, response.expiresInSeconds());
    }

    @Test
    void generatePresignedUploadUrl_shouldThrow_whenContentTypeNotAllowed() {
        when(storageProperties.getAllowedImageTypes()).thenReturn(List.of("image/jpeg"));

        PresignedUrlRequest req = new PresignedUrlRequest("image/gif", 50000);

        assertThrows(InvalidFileException.class,
                () -> profilePictureService.generatePresignedUploadUrl(req, "test@test.com"));
    }

    @Test
    void generatePresignedUploadUrl_shouldThrow_whenFileSizeZero() {
        when(storageProperties.getAllowedImageTypes()).thenReturn(List.of("image/jpeg"));

        PresignedUrlRequest req = new PresignedUrlRequest("image/jpeg", 0);

        assertThrows(InvalidFileException.class,
                () -> profilePictureService.generatePresignedUploadUrl(req, "test@test.com"));
    }

    @Test
    void generatePresignedUploadUrl_shouldThrow_whenFileSizeExceedsMax() {
        when(storageProperties.getAllowedImageTypes()).thenReturn(List.of("image/jpeg"));
        when(storageProperties.getMaxProfilePictureBytes()).thenReturn(5_242_880L);

        PresignedUrlRequest req = new PresignedUrlRequest("image/jpeg", 10_000_000);

        assertThrows(InvalidFileException.class,
                () -> profilePictureService.generatePresignedUploadUrl(req, "test@test.com"));
    }

    @Test
    void confirmUpload_shouldSaveKeyAndReturnViewUrl() {
        Users user = new Users();
        user.setEmail("test@test.com");

        when(usersRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(storageProperties.getProfilePictureBucket()).thenReturn("nonmus-profile-pics");
        when(storageProperties.getViewExpirySeconds()).thenReturn(900);
        when(storageService.generatePresignedViewUrl(
                eq("nonmus-profile-pics"), eq("users/test@test.com/new-pic.jpg"), eq(Duration.ofSeconds(900))))
                .thenReturn("https://view-url");

        ViewUrlResponse response = profilePictureService.confirmUpload(
                new ConfirmUploadRequest("users/test@test.com/new-pic.jpg"), "test@test.com");

        assertEquals("https://view-url", response.viewUrl());
        assertEquals(900, response.expiresInSeconds());
        assertEquals("users/test@test.com/new-pic.jpg", user.getProfilePicture());
        assertEquals(Provider.LOCAL, user.getProfilePictureProvider());
        verify(usersRepository).save(user);
    }

    @Test
    void confirmUpload_shouldDeleteOldPicture_whenPreviousExists() {
        Users user = new Users();
        user.setEmail("test@test.com");
        user.setProfilePicture("users/test@test.com/old-pic.jpg");

        when(usersRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(storageProperties.getProfilePictureBucket()).thenReturn("nonmus-profile-pics");
        when(storageProperties.getViewExpirySeconds()).thenReturn(900);
        when(storageService.generatePresignedViewUrl(anyString(), anyString(), any())).thenReturn("https://view-url");

        profilePictureService.confirmUpload(
                new ConfirmUploadRequest("users/test@test.com/new-pic.jpg"), "test@test.com");

        verify(storageService).deleteFile("nonmus-profile-pics", "users/test@test.com/old-pic.jpg");
    }

    @Test
    void confirmUpload_shouldNotDeleteOldPicture_whenPreviousMatchesNewKey() {
        Users user = new Users();
        user.setEmail("test@test.com");
        user.setProfilePicture("users/test@test.com/pic.jpg");

        when(usersRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(storageProperties.getProfilePictureBucket()).thenReturn("nonmus-profile-pics");
        when(storageProperties.getViewExpirySeconds()).thenReturn(900);
        when(storageService.generatePresignedViewUrl(anyString(), anyString(), any())).thenReturn("https://view-url");

        profilePictureService.confirmUpload(
                new ConfirmUploadRequest("users/test@test.com/pic.jpg"), "test@test.com");

        verify(storageService, never()).deleteFile(anyString(), anyString());
    }

    @Test
    void confirmUpload_shouldNotFail_whenDeleteOldPictureThrows() {
        Users user = new Users();
        user.setEmail("test@test.com");
        user.setProfilePicture("users/test@test.com/old-pic.jpg");

        when(usersRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(storageProperties.getProfilePictureBucket()).thenReturn("nonmus-profile-pics");
        when(storageProperties.getViewExpirySeconds()).thenReturn(900);
        doThrow(new RuntimeException("S3 error")).when(storageService)
                .deleteFile("nonmus-profile-pics", "users/test@test.com/old-pic.jpg");
        when(storageService.generatePresignedViewUrl(anyString(), anyString(), any())).thenReturn("https://view-url");

        ViewUrlResponse response = profilePictureService.confirmUpload(
                new ConfirmUploadRequest("users/test@test.com/new-pic.jpg"), "test@test.com");

        assertNotNull(response);
        verify(usersRepository).save(user);
    }

    @Test
    void confirmUpload_shouldNotFail_whenNoPrevProfilePicture() {
        Users user = new Users();
        user.setEmail("test@test.com");
        user.setProfilePicture(null);

        when(usersRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(storageProperties.getProfilePictureBucket()).thenReturn("nonmus-profile-pics");
        when(storageProperties.getViewExpirySeconds()).thenReturn(900);
        when(storageService.generatePresignedViewUrl(anyString(), anyString(), any())).thenReturn("https://view-url");

        ViewUrlResponse response = profilePictureService.confirmUpload(
                new ConfirmUploadRequest("users/test@test.com/new-pic.jpg"), "test@test.com");

        assertNotNull(response);
        assertEquals("https://view-url", response.viewUrl());
        assertEquals(900, response.expiresInSeconds());
        assertEquals("users/test@test.com/new-pic.jpg", user.getProfilePicture());
        assertEquals(Provider.LOCAL, user.getProfilePictureProvider());
        verify(usersRepository).save(user);
    }

    @Test
    void confirmUpload_shouldThrow_whenS3KeyDoesNotBelongToUser() {
        assertThrows(InvalidFileException.class,
                () -> profilePictureService.confirmUpload(
                        new ConfirmUploadRequest("users/other@test.com/pic.jpg"), "test@test.com"));
    }

    @Test
    void getViewUrl_shouldReturnEmpty_whenNoPictureSet() {
        Users user = new Users();
        user.setEmail("test@test.com");
        user.setProfilePicture(null);

        when(usersRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        ViewUrlResponse response = profilePictureService.getViewUrl("test@test.com");

        assertEquals("", response.viewUrl());
        assertEquals(0, response.expiresInSeconds());
    }

    @Test
    void getViewUrl_shouldReturnDirectUrl_whenProviderIsExternal() {
        Users user = new Users();
        user.setEmail("test@test.com");
        user.setProfilePicture("https://external-provider.com/avatar.jpg");
        user.setProfilePictureProvider(Provider.GOOGLE);

        when(usersRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(storageProperties.getViewExpirySeconds()).thenReturn(900);

        ViewUrlResponse response = profilePictureService.getViewUrl("test@test.com");

        assertEquals("https://external-provider.com/avatar.jpg", response.viewUrl());
        assertEquals(900, response.expiresInSeconds());
    }

    @Test
    void getViewUrl_shouldReturnPresignedUrl_whenProviderIsLocal() {
        Users user = new Users();
        user.setEmail("test@test.com");
        user.setProfilePicture("users/test@test.com/pic.jpg");
        user.setProfilePictureProvider(Provider.LOCAL);

        when(usersRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(storageProperties.getProfilePictureBucket()).thenReturn("nonmus-profile-pics");
        when(storageProperties.getViewExpirySeconds()).thenReturn(900);
        when(storageService.generatePresignedViewUrl(
                eq("nonmus-profile-pics"), eq("users/test@test.com/pic.jpg"), eq(Duration.ofSeconds(900))))
                .thenReturn("https://presigned-view-url");

        ViewUrlResponse response = profilePictureService.getViewUrl("test@test.com");

        assertEquals("https://presigned-view-url", response.viewUrl());
        assertEquals(900, response.expiresInSeconds());
    }

    @Test
    void generatePresignedUploadUrl_shouldHandlePngContentType() {
        when(storageProperties.getAllowedImageTypes()).thenReturn(List.of("image/png"));
        when(storageProperties.getMaxProfilePictureBytes()).thenReturn(5_242_880L);
        when(storageProperties.getProfilePictureBucket()).thenReturn("nonmus-profile-pics");
        when(storageProperties.getUploadExpirySeconds()).thenReturn(900);

        PresignedUrlRequest req = new PresignedUrlRequest("image/png", 50000);

        when(storageService.generatePresignedUploadUrl(
                eq("nonmus-profile-pics"), anyString(), eq("image/png"), eq(Duration.ofSeconds(900))))
                .thenReturn(new PresignedUploadResult("https://presigned-url", "users/test@test.com/uuid.png"));

        PresignedUrlResponse response = profilePictureService.generatePresignedUploadUrl(req, "test@test.com");

        assertEquals("https://presigned-url", response.presignedUrl());
        assertTrue(response.s3Key().endsWith(".png"));
    }

    @Test
    void generatePresignedUploadUrl_shouldHandleWebpContentType() {
        when(storageProperties.getAllowedImageTypes()).thenReturn(List.of("image/webp"));
        when(storageProperties.getMaxProfilePictureBytes()).thenReturn(5_242_880L);
        when(storageProperties.getProfilePictureBucket()).thenReturn("nonmus-profile-pics");
        when(storageProperties.getUploadExpirySeconds()).thenReturn(900);

        PresignedUrlRequest req = new PresignedUrlRequest("image/webp", 50000);

        when(storageService.generatePresignedUploadUrl(
                eq("nonmus-profile-pics"), anyString(), eq("image/webp"), eq(Duration.ofSeconds(900))))
                .thenReturn(new PresignedUploadResult("https://presigned-url", "users/test@test.com/uuid.webp"));

        PresignedUrlResponse response = profilePictureService.generatePresignedUploadUrl(req, "test@test.com");

        assertEquals("https://presigned-url", response.presignedUrl());
        assertTrue(response.s3Key().endsWith(".webp"));
    }

    @Test
    void generatePresignedUploadUrl_shouldHandleGifContentType() {
        when(storageProperties.getAllowedImageTypes()).thenReturn(List.of("image/gif"));
        when(storageProperties.getMaxProfilePictureBytes()).thenReturn(5_242_880L);
        when(storageProperties.getProfilePictureBucket()).thenReturn("nonmus-profile-pics");
        when(storageProperties.getUploadExpirySeconds()).thenReturn(900);

        PresignedUrlRequest req = new PresignedUrlRequest("image/gif", 50000);

        when(storageService.generatePresignedUploadUrl(
                eq("nonmus-profile-pics"), anyString(), eq("image/gif"), eq(Duration.ofSeconds(900))))
                .thenReturn(new PresignedUploadResult("https://presigned-url", "users/test@test.com/uuid.gif"));

        PresignedUrlResponse response = profilePictureService.generatePresignedUploadUrl(req, "test@test.com");

        assertEquals("https://presigned-url", response.presignedUrl());
        assertTrue(response.s3Key().endsWith(".gif"));
    }

    @Test
    void generatePresignedUploadUrl_shouldHandleUnmappedContentType() {
        when(storageProperties.getAllowedImageTypes()).thenReturn(List.of("image/avif"));
        when(storageProperties.getMaxProfilePictureBytes()).thenReturn(5_242_880L);
        when(storageProperties.getProfilePictureBucket()).thenReturn("nonmus-profile-pics");
        when(storageProperties.getUploadExpirySeconds()).thenReturn(900);

        PresignedUrlRequest req = new PresignedUrlRequest("image/avif", 50000);

        when(storageService.generatePresignedUploadUrl(
                eq("nonmus-profile-pics"), anyString(), eq("image/avif"), eq(Duration.ofSeconds(900))))
                .thenReturn(new PresignedUploadResult("https://presigned-url", "users/test@test.com/uuid"));

        PresignedUrlResponse response = profilePictureService.generatePresignedUploadUrl(req, "test@test.com");

        assertEquals("https://presigned-url", response.presignedUrl());
        assertEquals("users/test@test.com/uuid", response.s3Key());
    }

    @Test
    void generatePresignedUploadUrl_shouldThrow_whenContentTypeIsNull() {
        when(storageProperties.getAllowedImageTypes()).thenReturn(List.of("image/jpeg"));

        PresignedUrlRequest req = new PresignedUrlRequest(null, 50000);

        assertThrows(InvalidFileException.class,
                () -> profilePictureService.generatePresignedUploadUrl(req, "test@test.com"));
    }

    @Test
    void generatePresignedUploadUrl_shouldThrow_whenContentTypeIsEmpty() {
        when(storageProperties.getAllowedImageTypes()).thenReturn(List.of("image/jpeg"));

        PresignedUrlRequest req = new PresignedUrlRequest("", 50000);

        assertThrows(InvalidFileException.class,
                () -> profilePictureService.generatePresignedUploadUrl(req, "test@test.com"));
    }
}