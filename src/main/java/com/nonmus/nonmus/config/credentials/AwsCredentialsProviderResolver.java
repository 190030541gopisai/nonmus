package com.nonmus.nonmus.config.credentials;

import com.nonmus.nonmus.config.props.AwsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

@Component
@RequiredArgsConstructor
public class AwsCredentialsProviderResolver {
    private final AwsProperties properties;

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