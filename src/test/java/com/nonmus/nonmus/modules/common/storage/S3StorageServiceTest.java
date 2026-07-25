package com.nonmus.nonmus.modules.common.storage;

import com.nonmus.nonmus.modules.common.exception.StorageException;
import com.nonmus.nonmus.modules.common.storage.dto.PresignedUploadResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3StorageServiceTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Presigner s3Presigner;

    @InjectMocks
    private S3StorageService s3StorageService;

    @Test
    void generatePresignedUploadUrl_shouldReturnPresignedUrlAndKey() throws MalformedURLException {
        PresignedPutObjectRequest presignedResponse = mock(PresignedPutObjectRequest.class);
        when(presignedResponse.url()).thenReturn(new URL("https://bucket.s3.amazonaws.com/uploads/file.jpg?X-Amz-Signature=abc"));

        when(s3Presigner.presignPutObject(any(PutObjectPresignRequest.class)))
                .thenReturn(presignedResponse);

        PresignedUploadResult result = s3StorageService.generatePresignedUploadUrl(
                "my-bucket", "uploads/file.jpg", "image/jpeg", Duration.ofMinutes(15));

        assertEquals("https://bucket.s3.amazonaws.com/uploads/file.jpg?X-Amz-Signature=abc", result.presignedUrl());
        assertEquals("uploads/file.jpg", result.s3Key());
    }

    @Test
    void generatePresignedViewUrl_shouldReturnPresignedUrl() throws MalformedURLException {
        PresignedGetObjectRequest presignedResponse = mock(PresignedGetObjectRequest.class);
        when(presignedResponse.url()).thenReturn(new URL("https://bucket.s3.amazonaws.com/uploads/file.jpg?X-Amz-Signature=xyz"));

        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .thenReturn(presignedResponse);

        String url = s3StorageService.generatePresignedViewUrl(
                "my-bucket", "uploads/file.jpg", Duration.ofMinutes(15));

        assertEquals("https://bucket.s3.amazonaws.com/uploads/file.jpg?X-Amz-Signature=xyz", url);
    }

    @Test
    void deleteFile_shouldDeleteSuccessfully() {
        s3StorageService.deleteFile("my-bucket", "uploads/file.jpg");

        verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void deleteFile_shouldThrowStorageException_whenS3Fails() {
        doThrow(new RuntimeException("S3 error")).when(s3Client).deleteObject(any(DeleteObjectRequest.class));

        StorageException exception = assertThrows(StorageException.class,
                () -> s3StorageService.deleteFile("my-bucket", "uploads/file.jpg"));

        assertTrue(exception.getMessage().contains("uploads/file.jpg"));
    }
}