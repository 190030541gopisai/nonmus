package com.nonmus.nonmus.config;

import com.nonmus.nonmus.config.credentials.AwsCredentialsProviderResolver;
import com.nonmus.nonmus.config.props.AwsProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3ConfigTest {

    @Mock
    private AwsProperties properties;

    @Mock
    private AwsCredentialsProviderResolver credentialsResolver;

    private S3Config s3Config;

    @BeforeEach
    void setUp() {
        when(credentialsResolver.resolve())
                .thenReturn(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create("test", "test")
                        )
                );

        s3Config = new S3Config(properties, credentialsResolver);
    }

    @Test
    void s3Client_shouldCreateClient_whenNoCustomEndPoint() {
        when(properties.hasCustomEndpoint()).thenReturn(false);
        when(properties.getRegion()).thenReturn("us-east-1");

        S3Client client = s3Config.s3Client();

        assertNotNull(client);

        verify(credentialsResolver).resolve();
        verify(properties).getRegion();
        verify(properties).hasCustomEndpoint();
    }

    @Test
    void s3Client_shouldCreateClient_whenCustomEndpointConfigured() {
        when(properties.getRegion()).thenReturn("us-east-1");
        when(properties.hasCustomEndpoint()).thenReturn(true);
        when(properties.getEndpoint()).thenReturn("http://localhost:4566");

        S3Client client = s3Config.s3Client();

        assertNotNull(client);

        verify(properties).getEndpoint();
    }

    @Test
    void s3Presigner_shouldCreatePresigner_whenNoCustomEndpoint() {
        when(properties.getRegion()).thenReturn("us-east-1");
        when(properties.hasCustomEndpoint()).thenReturn(false);

        S3Presigner presigner = s3Config.s3Presigner();

        assertNotNull(presigner);

        verify(credentialsResolver, times(1)).resolve();
    }

    @Test
    void s3Presigner_shouldCreatePresigner_whenCustomEndpointProvided() {
        when(properties.getRegion()).thenReturn("us-east-1");
        when(properties.hasCustomEndpoint()).thenReturn(true);
        when(properties.getEndpoint()).thenReturn("http://localhost:4566");

        S3Presigner presigner = s3Config.s3Presigner();

        assertNotNull(presigner);

        verify(credentialsResolver, times(1)).resolve();
        verify(properties).getEndpoint();
    }

    @Test
    void s3Client_shouldUseDefaultRegion_whenRegionIsNull() {

        when(properties.getRegion()).thenReturn(null);
        when(properties.hasCustomEndpoint()).thenReturn(false);

        assertNotNull(s3Config.s3Client());
    }

    @Test
    void s3Client_shouldUseDefaultRegion_whenRegionIsBlank() {

        when(properties.getRegion()).thenReturn("");
        when(properties.hasCustomEndpoint()).thenReturn(false);

        assertNotNull(s3Config.s3Client());
    }
}