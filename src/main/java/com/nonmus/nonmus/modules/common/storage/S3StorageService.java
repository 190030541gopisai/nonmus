package com.nonmus.nonmus.modules.common.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.security.MessageDigest;

@Service
@RequiredArgsConstructor
public class S3StorageService implements StorageService {

    private final S3Client s3Client;

    @Value("${app.cdn.domain}")
    private String cdnDomain;

    @Override
    public String uploadFile(String bucketName, MultipartFile file, String folderPath) {
        try {
            byte[] fileBytes = file.getBytes();

            // Step 1: Generate content-based hash identifier
            String contentHash = calculateSHA256(fileBytes);

            // Step 2: Extract the original file extension safely
            String originalName = file.getOriginalFilename();
            String extension = "";
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }

            // Step 3: Create clean unique key path using content hash instead of UUID
            String sanitizedKey = folderPath + "/" + contentHash + extension;

            // Step 4: Check if this identical file already exists in the S3 bucket
            if (doesFileExist(bucketName, sanitizedKey)) {
                return sanitizedKey; // Skip upload and return the existing key immediately
            }

            // Step 5: Upload new file if it does not exist
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(sanitizedKey)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(fileBytes));
            return sanitizedKey;
        } catch (Exception e) {
            throw new RuntimeException("Cloud storage upload failed", e);
        }
    }

    @Override
    public String generatePublicUrl(String key) {
        if (key == null || key.isEmpty()) return "";
        String cleanDomain = cdnDomain.endsWith("/") ? cdnDomain.substring(0, cdnDomain.length() - 1) : cdnDomain;
        return cleanDomain + "/" + key;
    }

    @Override
    public void deleteFile(String bucketName, String key) {
        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(request);
        } catch (Exception e) {
            throw new RuntimeException("Cloud storage deletion failed", e); // Fixed error message context
        }
    }

    /**
     * Checks if an object exists in S3 using a lightweight metadata-only check.
     */
    private boolean doesFileExist(String bucketName, String key) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.headObject(headObjectRequest);
            return true; // File exists
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                return false; // File does not exist
            }
            throw e; // Rethrow other actual AWS errors (e.g., 403 Forbidden)
        }
    }

    /**
     * Computes a SHA-256 hex string from byte data to uniquely fingerprint file content.
     */
    private String calculateSHA256(byte[] data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data);
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
