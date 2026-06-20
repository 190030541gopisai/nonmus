package com.nonmus.nonmus.modules.common.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    // Accepts any dynamic bucket name
    String uploadFile(String bucketName, MultipartFile file, String folderPath);
    String generatePublicUrl(String key);
}
