package com.nonmus.nonmus.modules.user.dto.request;

/**
 * Sent by the client after a successful direct-to-S3 upload to commit the file key
 * to the database.
 *
 * s3Key — the object key returned in PresignedUrlResponse. The backend validates
 *         that this key belongs to the authenticated user before saving it.
 */
public record ConfirmUploadRequest(String s3Key) {}
