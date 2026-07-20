package com.nonmus.nonmus.config.credentials;

import com.nonmus.nonmus.config.props.AwsProperties;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

@Component
public class AwsCredentialsProviderResolver {

    private final AwsProperties properties;

    public AwsCredentialsProviderResolver(AwsProperties properties) {
        this.properties = properties;
    }

    /**
     * Returns explicit credentials for local dev, or defaults to IAM Role/Env for Prod.
     * Adheres to Open/Closed Principle: New strategies can be added without modifying this method.
     */
    public AwsCredentialsProvider resolve() {
        if (properties.hasExplicitCredentials()) {
            return StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(properties.getAccessKey(), properties.getSecretKey())
            );
        }
        // Fallback to standard AWS chain (IAM Role, Env Vars, Profile)
        return DefaultCredentialsProvider.create();
    }
}