package com.nonmus.nonmus.config.credentials;

import com.nonmus.nonmus.config.props.AwsProperties;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class AwsCredentialsProviderResolverTest {
    @Test
    void shouldReturnStaticCredentialsProviderWhenCredentialsExist(){
        AwsProperties properties = new AwsProperties();
        properties.setAccessKey("test-access");
        properties.setSecretKey("test-secret");

        AwsCredentialsProviderResolver resolver =
                new AwsCredentialsProviderResolver(properties);

        AwsCredentialsProvider provider = resolver.resolve();

        assertInstanceOf(StaticCredentialsProvider.class, provider);

        AwsCredentials credentials = provider.resolveCredentials();
        assertEquals("test-access", credentials.accessKeyId());
        assertEquals("test-secret", credentials.secretAccessKey());
    }

    @Test
    void shouldReturnDefaultCredentialsProviderWhenCredentialsMissing() {
        AwsProperties properties = new AwsProperties();

        AwsCredentialsProviderResolver resolver =
                new AwsCredentialsProviderResolver(properties);

        AwsCredentialsProvider provider = resolver.resolve();

        assertInstanceOf(DefaultCredentialsProvider.class, provider);
    }
}
