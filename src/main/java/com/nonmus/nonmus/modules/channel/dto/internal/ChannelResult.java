package com.nonmus.nonmus.modules.channel.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelResult {
    private UUID channelId;
    private String name;
    private String description;
    private String logo;
    private String handle;
    private Long subscribersCount;
    private Instant createdAt;
}
