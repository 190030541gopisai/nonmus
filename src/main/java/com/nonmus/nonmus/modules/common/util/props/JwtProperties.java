package com.nonmus.nonmus.modules.common.util.props;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
@Validated
public class JwtProperties {
    @NotBlank
    private String secretKey;

    @Min(1)
    private long jwtExpirationMs;

    @Min(1)
    private long refreshTokenExpirationMs;

    @Min(1)
    private long rememberMeRefreshTokenExpirationMs;
}
