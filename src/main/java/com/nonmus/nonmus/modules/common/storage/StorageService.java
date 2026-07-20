package com.nonmus.nonmus.modules.common.storage;

import com.nonmus.nonmus.modules.common.storage.dto.PresignedUploadResult;

import java.time.Duration;

public interface StorageService {

    /**
     * Generates a short-lived presigned PUT URL that allows the client to upload
     * a file directly to S3 without routing bytes through the backend.
     *
     * @param bucketName  the target S3 bucket
     * @param key         the full S3 object key (e.g. "users/email@x.com/uuid.jpg")
     * @param contentType the MIME type the client will use in the PUT request Content-Type header
     * @param expiry      how long the presigned URL remains valid
     * @return            a PresignedUploadResult containing the presigned URL and the key
     */
    PresignedUploadResult generatePresignedUploadUrl(String bucketName, String key,
                                                     String contentType, Duration expiry);

    /**
     * Generates a short-lived presigned GET URL that allows authenticated access
     * to a private S3 object. The S3 bucket must have all public access blocked.
     *
     * @param bucketName the target S3 bucket
     * @param key        the S3 object key to read
     * @param expiry     how long the presigned URL remains valid
     * @return           a presigned HTTPS URL the client can use to display the file
     */
    String generatePresignedViewUrl(String bucketName, String key, Duration expiry);

    /**
     * Permanently deletes an object from S3. Always called server-side — we never
     * issue presigned DELETE URLs to clients.
     *
     * @param bucketName the target S3 bucket
     * @param key        the S3 object key to delete
     */
    void deleteFile(String bucketName, String key);
}
