import { useState, useCallback } from "react";
import {
  requestPresignedUploadUrl,
  uploadFileToS3,
  confirmUpload,
  getProfilePictureViewUrl,
} from "../api/storageApi";

const ALLOWED_TYPES = ["image/jpeg", "image/png", "image/webp", "image/gif"];
const MAX_BYTES = 5 * 1024 * 1024; // 5 MB

/**
 * Manages the full profile picture lifecycle:
 * - Loading the current view URL (with expiry-aware refresh)
 * - Running the 3-step presigned upload flow
 * - Tracking upload progress and error state
 */
export const useProfilePicture = () => {
  const [viewUrl, setViewUrl] = useState("");
  const [uploading, setUploading] = useState(false);
  const [uploadProgress, setUploadProgress] = useState(0); // 0-100
  const [error, setError] = useState("");
  const [success, setSuccess] = useState(false);

  /**
   * Fetches a fresh presigned GET URL from the backend.
   * Safe to call on mount and whenever the current URL expires.
   */
  const loadViewUrl = useCallback(async () => {
    try {
      const data = await getProfilePictureViewUrl();
      setViewUrl(data.viewUrl || "");
    } catch {
      // Not a fatal error — user just has no picture yet
      setViewUrl("");
    }
  }, []);

  /**
   * Runs the full 3-step upload flow:
   * 1. Backend validates file and returns presigned PUT URL
   * 2. File is PUT directly to S3 (no backend involved)
   * 3. Backend confirms the key and returns a fresh view URL
   *
   * Retries once on 403 (expired presigned URL).
   */
  const uploadProfilePicture = useCallback(async (file) => {
    setError("");
    setSuccess(false);

    // Client-side pre-validation (mirrors backend rules)
    if (!ALLOWED_TYPES.includes(file.type)) {
      setError(`File type not allowed. Use JPEG, PNG, WebP, or GIF.`);
      return;
    }
    if (file.size > MAX_BYTES) {
      setError("File is too large. Maximum size is 5 MB.");
      return;
    }

    setUploading(true);
    setUploadProgress(10);

    const attemptUpload = async () => {
      // Step 1 — request presigned PUT URL
      const { presignedUrl, s3Key } = await requestPresignedUploadUrl(
        file.type,
        file.size
      );
      setUploadProgress(30);

      // Step 2 — upload directly to S3
      await uploadFileToS3(presignedUrl, file);
      setUploadProgress(70);

      // Step 3 — confirm with backend and get the view URL
      const { viewUrl: freshUrl } = await confirmUpload(s3Key);
      setUploadProgress(100);

      return freshUrl;
    };

    try {
      const freshUrl = await attemptUpload();
      setViewUrl(freshUrl);
      setSuccess(true);
    } catch (err) {
      // If S3 returned 403 the presigned URL expired — retry once with a fresh URL
      if (err?.response?.status === 403) {
        try {
          const freshUrl = await attemptUpload();
          setViewUrl(freshUrl);
          setSuccess(true);
        } catch (retryErr) {
          setError("Upload failed after retry. Please try again.");
        }
      } else {
        const message =
          err?.response?.data?.message || "Upload failed. Please try again.";
        setError(message);
      }
    } finally {
      setUploading(false);
      setTimeout(() => {
        setUploadProgress(0);
        setSuccess(false);
      }, 3000);
    }
  }, []);

  /**
   * Called by the <img> onError handler when a presigned GET URL has expired.
   * Silently fetches a fresh URL and updates state — the image re-renders automatically.
   */
  const handleViewUrlExpired = useCallback(async () => {
    await loadViewUrl();
  }, [loadViewUrl]);

  return {
    viewUrl,
    uploading,
    uploadProgress,
    error,
    success,
    loadViewUrl,
    uploadProfilePicture,
    handleViewUrlExpired,
  };
};
