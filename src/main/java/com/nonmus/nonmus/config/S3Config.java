package com.nonmus.nonmus.config;

import com.nonmus.nonmus.config.credentials.AwsCredentialsProviderResolver;
import com.nonmus.nonmus.config.props.AwsProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
public class S3Config {

    private final AwsProperties properties;
    private final AwsCredentialsProviderResolver credentialsResolver;

    public S3Config(AwsProperties properties, AwsCredentialsProviderResolver credentialsResolver) {
        this.properties = properties;
        this.credentialsResolver = credentialsResolver;
    }

    @Bean
    public S3Client s3Client() {
        S3ClientBuilder builder = S3Client.builder()
                .region(resolveRegion())
                .credentialsProvider(credentialsResolver.resolve());

        if (properties.hasCustomEndpoint()) {
            builder.endpointOverride(URI.create(properties.getS3Endpoint()));
        }

        return builder
                .forcePathStyle(true)
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        S3Presigner.Builder builder = S3Presigner.builder()
                .region(resolveRegion())
                .credentialsProvider(credentialsResolver.resolve());

        if (properties.hasCustomEndpoint()) {
            builder.endpointOverride(URI.create(properties.getS3Endpoint()));
        }

        return builder.build();
    }

    private Region resolveRegion() {
        String regionStr = properties.getRegion();
        if (regionStr == null || regionStr.trim().isEmpty()) {
            return Region.US_EAST_1;
        }
        return Region.of(regionStr.toLowerCase().replace("_", "-"));
    }
}
