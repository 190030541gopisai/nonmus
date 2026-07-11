package com.nonmus.nonmus.modules.user.dto.response;

/**
 * Returned to the client after requesting a presigned upload URL.
 *
 * presignedUrl     — the short-lived S3 PUT URL. Client must HTTP PUT the file directly
 *                    to this URL within expiresInSeconds seconds.
 * s3Key            — the object key that will be created in S3. Must be included in the
 *                    subsequent confirm request body.
 * expiresInSeconds — how many seconds until presignedUrl becomes invalid. Frontend should
 *                    use this to warn users if they take too long before uploading.
 */
public record PresignedUrlResponse(String presignedUrl, String s3Key, int expiresInSeconds) {}
