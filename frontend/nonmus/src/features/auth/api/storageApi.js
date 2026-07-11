import { apiClient } from "../../../api/client";
import axios from "axios";

const PREFIX = "/v1/users/profile-picture";

/**
 * Step 1 — Request a presigned PUT URL from the backend.
 * The backend validates contentType and fileSize before issuing the URL.
 */
export const requestPresignedUploadUrl = async (contentType, fileSize) => {
  const response = await apiClient.post(`${PREFIX}/presigned-url`, {
    contentType,
    fileSize,
  });
  return response.data; // { presignedUrl, s3Key, expiresInSeconds }
};

/**
 * Step 2 — Upload the file directly to S3 using the presigned PUT URL.
 *
 * CRITICAL: Uses a plain axios instance, NOT apiClient.
 * S3 rejects requests that carry both an Authorization header
 * and presign query params — they must not be mixed.
 */
export const uploadFileToS3 = async (presignedUrl, file) => {
  await axios.put(presignedUrl, file, {
    headers: { "Content-Type": file.type },
  });
};

/**
 * Step 3 — Confirm the upload with the backend.
 * The backend validates key ownership, deletes the old picture,
 * saves the new key, and returns a fresh presigned GET URL.
 */
export const confirmUpload = async (s3Key) => {
  const response = await apiClient.put(`${PREFIX}/confirm`, { s3Key });
  return response.data; // { viewUrl, expiresInSeconds }
};

/**
 * On-demand — Get a fresh presigned GET URL for the current user's profile picture.
 * Call on page load and whenever the previous URL has expired (onerror on <img>).
 */
export const getProfilePictureViewUrl = async () => {
  const response = await apiClient.get(`${PREFIX}/view-url`);
  return response.data; // { viewUrl, expiresInSeconds }
};
