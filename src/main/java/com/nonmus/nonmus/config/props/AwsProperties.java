package com.nonmus.nonmus.config.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "aws")
@Data
public class AwsProperties {
    private String region = "us-east-1";
    private String s3Endpoint; // Removed default to distinguish "not set" vs "empty"
    private String accessKey;
    private String secretKey;

    public boolean hasExplicitCredentials() {
        return accessKey != null && !accessKey.trim().isEmpty() &&
                secretKey != null && !secretKey.trim().isEmpty();
    }

    public boolean hasCustomEndpoint() {
        return s3Endpoint != null && !s3Endpoint.trim().isEmpty();
    }
}
