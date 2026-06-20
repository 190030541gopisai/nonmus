package com.nonmus.nonmus.modules.common.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3StorageService implements StorageService {

    private final S3Client s3Client;

    @Value("${app.cdn.domain}")
    private String cdnDomain;

    @Override
    public String uploadFile(String bucketName, MultipartFile file, String folderPath) {
        try {
            // Extract the real file extension dynamically
            String originalName = file.getOriginalFilename();
            String extension = "";
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }

            // Create a completely clean unique key path
            String sanitizedKey = folderPath + "/" + UUID.randomUUID() + extension;

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName) // Dynamic bucket assignment
                    .key(sanitizedKey)
                    .contentType(file.getContentType()) // Dynamic content type (image/png, video/mp4, etc)
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));
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
}
