package com.nonmus.nonmus.modules.user.controller;

import com.nonmus.nonmus.modules.common.util.AuthUtil;
import com.nonmus.nonmus.modules.user.dto.request.ConfirmUploadRequest;
import com.nonmus.nonmus.modules.user.dto.request.PresignedUrlRequest;
import com.nonmus.nonmus.modules.user.dto.response.PresignedUrlResponse;
import com.nonmus.nonmus.modules.user.dto.response.ViewUrlResponse;
import com.nonmus.nonmus.modules.user.enums.Provider;
import com.nonmus.nonmus.modules.user.service.ProfilePictureService;
import com.nonmus.nonmus.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/profile-picture")
@RequiredArgsConstructor
public class ProfilePictureController {

    private final ProfilePictureService profilePictureService;

    /**
     * Step 1 — Request a presigned PUT URL.
     *
     * The client sends the file's content type and size for server-side validation.
     * On success, the backend returns a short-lived S3 presigned URL and the S3 key.
     * The client should then PUT the file directly to S3 using the returned URL,
     * with no Authorization header (the presigned URL itself contains the auth).
     */
    @PostMapping("/presigned-url")
    public ResponseEntity<PresignedUrlResponse> getPresignedUploadUrl(
            @RequestBody PresignedUrlRequest request) {
        AuthenticatedUser authenticatedUser = AuthUtil.getPrincipal();
        String email = authenticatedUser.getEmail();
        Provider provider = authenticatedUser.getProvider();

        PresignedUrlResponse response = profilePictureService.generatePresignedUploadUrl(request, email, provider);
        return ResponseEntity.ok(response);
    }

    /**
     * Step 3 — Confirm the upload.
     *
     * Called after the client has successfully PUT the file to S3.
     * The backend validates the s3Key ownership, deletes the old picture,
     * saves the new key, and returns a fresh presigned GET URL for immediate rendering.
     */
    @PutMapping("/confirm")
    public ResponseEntity<ViewUrlResponse> confirmUpload(
            @RequestBody ConfirmUploadRequest request) {
        AuthenticatedUser authenticatedUser = AuthUtil.getPrincipal();
        String email = authenticatedUser.getEmail();
        Provider provider = authenticatedUser.getProvider();

        ViewUrlResponse response = profilePictureService.confirmUpload(request, email, provider);
        return ResponseEntity.ok(response);
    }

    /**
     * On-demand view URL.
     *
     * Returns a fresh presigned GET URL for the authenticated user's profile picture.
     * Call this on page load and whenever the previous URL expires (onerror handler).
     * Always requires authentication — the S3 bucket has no public access.
     */
    @GetMapping("/view-url")
    public ResponseEntity<ViewUrlResponse> getViewUrl() {
        AuthenticatedUser authenticatedUser = AuthUtil.getPrincipal();
        String email = authenticatedUser.getEmail();
        Provider provider = authenticatedUser.getProvider();

        ViewUrlResponse response = profilePictureService.getViewUrl(email, provider);
        return ResponseEntity.ok(response);
    }
}
