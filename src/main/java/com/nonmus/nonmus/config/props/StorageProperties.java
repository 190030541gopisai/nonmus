package com.nonmus.nonmus.config.props;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotNull; // <--- Import this, NOT lombok.NonNull

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "app.storage")
@Data
@Validated
public class StorageProperties {
    @NotNull(message = "Profile picture bucket name is required")
    private String profilePictureBucket;

    @NotNull(message = "Channel logo bucket name is required")
    private String channelLogoBucket;

    @Positive(message = "Upload expiry seconds must be positive")
    private int uploadExpirySeconds;

    @Positive(message = "View expiry seconds must be positive")
    private int viewExpirySeconds;

    @Positive(message = "Max profile picture size must be positive")
    private long maxProfilePictureBytes;

    @NotEmpty(message = "Allowed image types list cannot be empty")
    private List<String> allowedImageTypes;
}
