package com.nonmus.nonmus.modules.common.storage;

import com.nonmus.nonmus.modules.common.storage.dto.PresignedUploadResult;
import com.nonmus.nonmus.modules.common.exception.StorageException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class S3StorageService implements StorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    /**
     * Generates a short-lived presigned PUT URL.
     *
     * The client uses this URL to upload the file directly to S3.
     * The backend never handles the file bytes — only the metadata (key, content type, expiry).
     *
     * The Content-Type constraint embedded in the presigned URL means S3 will reject
     * uploads that use a different MIME type, providing a second layer of type enforcement.
     */
    @Override
    public PresignedUploadResult generatePresignedUploadUrl(String bucketName, String key,
                                                             String contentType, Duration expiry) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(expiry)
                .putObjectRequest(putObjectRequest)
                .build();

        String presignedUrl = s3Presigner.presignPutObject(presignRequest)
                .url()
                .toString();

        return new PresignedUploadResult(presignedUrl, key);
    }

    /**
     * Generates a short-lived presigned GET URL.
     *
     * The S3 bucket must have all public access blocked. This URL embeds AWS
     * Signature V4 credentials in the query string, so no Authorization header
     * is needed by the client. The URL is safe to open in an incognito browser
     * but expires after the configured duration.
     */
    @Override
    public String generatePresignedViewUrl(String bucketName, String key, Duration expiry) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(expiry)
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest)
                .url()
                .toString();
    }

    /**
     * Permanently deletes an S3 object.
     * Always called server-side — we never issue presigned DELETE URLs to clients.
     */
    @Override
    public void deleteFile(String bucketName, String key) {
        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(request);
        } catch (Exception e) {
            throw new StorageException("Cloud storage deletion failed for key: " + key, e);
        }
    }
}
