package com.nonmus.nonmus.modules.user.dto.response;

/**
 * Returned to the client whenever a presigned GET URL is needed to display a file.
 *
 * viewUrl          — the short-lived S3 GET URL. Safe to use as an <img src> or video src.
 *                    The S3 bucket is fully private; auth is embedded in the URL signature.
 * expiresInSeconds — seconds until viewUrl becomes invalid. Frontend should not cache
 *                    this URL longer than this value.
 */
public record ViewUrlResponse(String viewUrl, int expiresInSeconds) {}
