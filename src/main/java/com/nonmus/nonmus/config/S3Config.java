package com.nonmus.nonmus.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

import java.net.URI;

@Configuration
public class S3Config {

    // Adding : ensures Spring boots up even if the property is absent in prod
    @Value("${aws.s3.endpoint:}")
    private String endpoint;

    @Value("${aws.region:us-east-1}") // Default to us-east-1 if omitted
    private String region;

    @Value("${aws.access-key:}")
    private String accessKey;

    @Value("${aws.secret-key:}")
    private String secretKey;

    @Bean
    public S3Client s3Client() {
        S3ClientBuilder builder = S3Client.builder();

        // 1. Handle dynamic regions (accepts "us-east-1", "us-west-2", etc.)
        if (region != null && !region.trim().isEmpty()) {
            builder.region(Region.of(region.toLowerCase().replace("_", "-")));
        }

        // 2. Handle local override (floci)
        if (endpoint != null && !endpoint.trim().isEmpty()) {
            builder.endpointOverride(URI.create(endpoint));
        }

        // 3. Handle explicit credentials (Local dev), otherwise falls back to IAM Role (Prod)
        if (accessKey != null && !accessKey.trim().isEmpty() &&
                secretKey != null && !secretKey.trim().isEmpty()) {
            builder.credentialsProvider(
                    StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(accessKey, secretKey)));
        }

        return builder
                .forcePathStyle(true)
                .build();
    }
}
