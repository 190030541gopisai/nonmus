package com.nonmus.nonmus.modules.user.dto.request;

/**
 * Sent by the client to request a presigned upload URL for a profile picture.
 *
 * contentType — MIME type the client intends to upload (e.g. "image/jpeg").
 *               Validated server-side against the allowed-image-types allowlist.
 * fileSize    — size in bytes. Validated server-side against max-profile-picture-bytes.
 *               Prevents the backend from issuing a URL for a file it will refuse later.
 */
public record PresignedUrlRequest(String contentType, long fileSize) {}
