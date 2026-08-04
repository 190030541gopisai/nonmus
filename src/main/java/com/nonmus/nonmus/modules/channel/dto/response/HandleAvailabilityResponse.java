package com.nonmus.nonmus.modules.channel.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HandleAvailabilityResponse {
    private boolean isAvailable;
}
