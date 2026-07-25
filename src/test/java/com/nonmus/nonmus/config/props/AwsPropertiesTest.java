package com.nonmus.nonmus.config.props;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

public class AwsPropertiesTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(TestConfig.class);

    @EnableConfigurationProperties(AwsProperties.class)
    static class TestConfig {}

    @Test
    void shouldBindConfigurationProperties() {
        contextRunner
                .withPropertyValues(
                        "aws.region=us-east-1",
                        "aws.endpoint=http://localhost.floci.io:4566",
                        "aws.access-key=test",
                        "aws.secret-key=test"
                )
                .run(context -> {
                    assertThat(context).hasSingleBean(AwsProperties.class);
                    AwsProperties properties = context.getBean(AwsProperties.class);

                    assertThat(properties.getRegion()).isEqualTo("us-east-1");
                    assertThat(properties.getEndpoint()).isEqualTo("http://localhost.floci.io:4566");
                    assertThat(properties.getAccessKey()).isEqualTo("test");
                    assertThat(properties.getSecretKey()).isEqualTo("test");

                    assertThat(properties.hasCustomEndpoint()).isTrue();
                    assertThat(properties.hasExplicitCredentials()).isTrue();
                });
    }

    @Test
    void shouldUseDefaultValuesWhenNoPropertiesAreConfigured() {
        contextRunner
                .run(context -> {
                    assertThat(context).hasSingleBean(AwsProperties.class);
                    AwsProperties properties = context.getBean(AwsProperties.class);

                    assertThat(properties.getRegion()).isEqualTo("us-east-1");
                    assertThat(properties.getEndpoint()).isNull();
                    assertThat(properties.getAccessKey()).isNull();
                    assertThat(properties.getSecretKey()).isNull();

                    assertThat(properties.hasCustomEndpoint()).isFalse();
                    assertThat(properties.hasExplicitCredentials()).isFalse();
                });
    }

    @Test
    void hasCustomEndpoint_shouldReturnFalse_whenEndpointIsBlank() {
        contextRunner
                .withPropertyValues("aws.endpoint=   ")
                .run(context -> {
                    AwsProperties properties = context.getBean(AwsProperties.class);
                    assertThat(properties.hasCustomEndpoint()).isFalse();
                });
    }

    @Test
    void hasExplicitCredentials_shouldReturnFalse_whenCredentialsAreBlank() {
        contextRunner
                .withPropertyValues(
                        "aws.access-key=   ",
                        "aws.secret-key=   "
                )
                .run(context -> {
                    AwsProperties properties = context.getBean(AwsProperties.class);
                    assertThat(properties.hasExplicitCredentials()).isFalse();
                });
    }

    @Test
    void hasExplicitCredentials_shouldReturnFalse_whenSecretKeyIsNull() {
        contextRunner
                .withPropertyValues(
                        "aws.access-key=test"
                )
                .run(context -> {
                    AwsProperties properties = context.getBean(AwsProperties.class);
                    assertThat(properties.hasExplicitCredentials()).isFalse();
                });
    }

    @Test
    void hasExplicitCredentials_shouldReturnFalse_whenAccessKeyIsNull() {
        contextRunner
                .withPropertyValues(
                        "aws.secret-key=test"
                )
                .run(context -> {
                    AwsProperties properties = context.getBean(AwsProperties.class);
                    assertThat(properties.hasExplicitCredentials()).isFalse();
                });
    }
}
