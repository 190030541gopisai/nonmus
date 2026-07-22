package com.nonmus.nonmus.config.props;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class StoragePropertiesTest {

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner()
                    .withUserConfiguration(TestConfig.class);

    @EnableConfigurationProperties(StorageProperties.class)
    static class TestConfig {
    }

    @Test
    void shouldLoadStorageProperties() {
        contextRunner
                .withPropertyValues(
                        "app.storage.profile-picture-bucket=nonmus-profile-pics",
                        "app.storage.upload-expiry-seconds=600",
                        "app.storage.view-expiry-seconds=3600",
                        "app.storage.max-profile-picture-bytes=5242880",
                        "app.storage.allowed-image-types=image/jpeg,image/png,image/webp,image/gif",
                        "app.storage.channel-logo-bucket=nonmus-channel"
                )
                .run(context -> {
                    assertThat(context).hasSingleBean(StorageProperties.class);

                    StorageProperties properties = context.getBean(StorageProperties.class);

                    assertThat(properties.getProfilePictureBucket())
                            .isEqualTo("nonmus-profile-pics");

                    assertThat(properties.getUploadExpirySeconds())
                            .isEqualTo(600);

                    assertThat(properties.getViewExpirySeconds())
                            .isEqualTo(3600);

                    assertThat(properties.getMaxProfilePictureBytes())
                            .isEqualTo(5_242_880L);

                    assertThat(properties.getChannelLogoBucket())
                            .isEqualTo("nonmus-channel");

                    assertThat(properties.getAllowedImageTypes())
                            .containsExactly(
                                    "image/jpeg",
                                    "image/png",
                                    "image/webp",
                                    "image/gif"
                            );
                });
    }
}