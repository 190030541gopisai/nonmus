package com.nonmus.nonmus.modules.common.storage.dto;

/**
 * Holds the result of a presigned upload URL generation.
 *
 * presignedUrl — the short-lived S3 PUT URL the client uses to upload directly to S3.
 * s3Key        — the object key that will be written to S3. Must be sent back to the
 *                backend in the confirm step so it can be persisted to the database.
 */
public record PresignedUploadResult(String presignedUrl, String s3Key) {}
