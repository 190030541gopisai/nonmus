package com.nonmus.nonmus.modules.user.service;

import com.nonmus.nonmus.config.props.StorageProperties;
import com.nonmus.nonmus.modules.common.exception.InvalidFileException;
import com.nonmus.nonmus.modules.common.storage.StorageService;
import com.nonmus.nonmus.modules.common.storage.dto.PresignedUploadResult;
import com.nonmus.nonmus.modules.user.dto.request.ConfirmUploadRequest;
import com.nonmus.nonmus.modules.user.dto.request.PresignedUrlRequest;
import com.nonmus.nonmus.modules.user.dto.response.PresignedUrlResponse;
import com.nonmus.nonmus.modules.user.dto.response.ViewUrlResponse;
import com.nonmus.nonmus.modules.user.entity.Users;
import com.nonmus.nonmus.modules.user.enums.Provider;
import com.nonmus.nonmus.modules.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfilePictureService {

    private final UsersRepository usersRepository;
    private final StorageService storageService;
    private final StorageProperties storageProperties;

    /**
     * Step 1 of the upload flow.
     *
     * Validates the requested file type and size, then generates a short-lived
     * presigned PUT URL the client can use to upload directly to S3.
     * The backend never touches the file bytes.
     *
     * @param req   content type and file size declared by the client
     * @param email authenticated user's email (used to namespace the S3 key)
     * @return      presigned PUT URL, the S3 key, and expiry duration
     */
    public PresignedUrlResponse generatePresignedUploadUrl(PresignedUrlRequest req, String email) {
        validateContentType(req.contentType());
        validateFileSize(req.fileSize());

        String extension = extensionForMimeType(req.contentType());
        // UUID in the key prevents enumeration — no one can guess another user's key
        String s3Key = "users/" + email + "/" + UUID.randomUUID() + extension;

        PresignedUploadResult result = storageService.generatePresignedUploadUrl(
                storageProperties.getProfilePictureBucket(),
                s3Key,
                req.contentType(),
                Duration.ofSeconds(storageProperties.getUploadExpirySeconds()));

        log.info("Presigned upload URL generated for user={} key={}", email, s3Key);

        return new PresignedUrlResponse(
                result.presignedUrl(),
                result.s3Key(),
                storageProperties.getUploadExpirySeconds());
    }

    /**
     * Step 3 of the upload flow (step 2 is the client uploading directly to S3).
     *
     * Confirms that the upload succeeded by saving the S3 key to the user's profile.
     * Also deletes the user's previous profile picture from S3 (if one existed) and
     * returns a fresh presigned GET URL so the UI can display the new image immediately.
     *
     * Security: the s3Key is validated to start with "users/{email}/" — this prevents
     * a malicious client from pointing another user's key at their own account.
     *
     * @param req   the s3Key confirmed by the client
     * @param email authenticated user's email
     * @return      a fresh presigned GET URL for the newly uploaded picture
     */
    @Transactional
    public ViewUrlResponse confirmUpload(ConfirmUploadRequest req, String email) {
        String expectedPrefix = "users/" + email + "/";
        if (!req.s3Key().startsWith(expectedPrefix)) {
            throw new InvalidFileException(
                    "Invalid file key: key does not belong to the authenticated user.");
        }

        Users user = usersRepository.findByEmail(email).orElseThrow();

        // Delete old profile picture from S3 before replacing it
        String previousKey = user.getProfilePicture();
        if (StringUtils.hasText(previousKey) && !previousKey.equals(req.s3Key())) {
            try {
                storageService.deleteFile(storageProperties.getProfilePictureBucket(), previousKey);
                log.info("Deleted old profile picture key={} for user={}", previousKey, email);
            } catch (Exception e) {
                // Log but do not fail — old file cleanup is best-effort
                log.warn("Failed to delete old profile picture key={} for user={}: {}",
                        previousKey, email, e.getMessage());
            }
        }

        user.setProfilePicture(req.s3Key());
        user.setProfilePictureProvider(Provider.LOCAL);
        usersRepository.save(user);

        log.info("Profile picture confirmed for user={} key={}", email, req.s3Key());

        String viewUrl = storageService.generatePresignedViewUrl(
                storageProperties.getProfilePictureBucket(),
                req.s3Key(),
                Duration.ofSeconds(storageProperties.getViewExpirySeconds()));

        return new ViewUrlResponse(viewUrl, storageProperties.getViewExpirySeconds());
    }

    /**
     * On-demand presigned GET URL for displaying a user's profile picture.
     *
     * Called by the frontend whenever it needs to render the image (e.g. on page load,
     * after the current URL expires). Always requires authentication — the bucket is
     * fully private and has no public access.
     *
     * @param email authenticated user's email
     * @return      a fresh presigned GET URL, or an empty ViewUrlResponse if no picture is set
     */
    public ViewUrlResponse getViewUrl(String email) {
        Users user = usersRepository.findByEmail(email).orElseThrow();
        String key = user.getProfilePicture();
        Provider profilePicProvider = user.getProfilePictureProvider();

        if (!StringUtils.hasText(key)) {
            // No profile picture set — return empty response, not an error
            return new ViewUrlResponse("", 0);
        }

        if(profilePicProvider != Provider.LOCAL) {
            return new ViewUrlResponse(key, storageProperties.getViewExpirySeconds());
        }

        String viewUrl = storageService.generatePresignedViewUrl(
                storageProperties.getProfilePictureBucket(),
                key,
                Duration.ofSeconds(storageProperties.getViewExpirySeconds()));

        return new ViewUrlResponse(viewUrl, storageProperties.getViewExpirySeconds());
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private void validateContentType(String contentType) {
        if (!StringUtils.hasText(contentType)
                || !storageProperties.getAllowedImageTypes().contains(contentType)) {
            throw new InvalidFileException(
                    "File type '" + contentType + "' is not allowed. " +
                    "Accepted types: " + String.join(", ", storageProperties.getAllowedImageTypes()));
        }
    }

    private void validateFileSize(long fileSize) {
        if (fileSize <= 0) {
            throw new InvalidFileException("File size must be greater than zero.");
        }
        if (fileSize > storageProperties.getMaxProfilePictureBytes()) {
            long maxMb = storageProperties.getMaxProfilePictureBytes() / (1024 * 1024);
            throw new InvalidFileException(
                    "File size exceeds the maximum allowed size of " + maxMb + " MB.");
        }
    }

    /**
     * Maps a MIME type to a safe file extension.
     * Falls back to an empty string so keys remain valid even for unmapped types.
     */
    private String extensionForMimeType(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png"  -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif"  -> ".gif";
            default           -> "";
        };
    }
}
