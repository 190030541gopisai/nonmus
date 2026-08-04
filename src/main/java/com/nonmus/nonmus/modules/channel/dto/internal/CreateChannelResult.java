package com.nonmus.nonmus.modules.channel.dto.internal;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class CreateChannelResult {
    private UUID channelId;
    private String handle;
    private String name;
    private String description;
    private String logo;
    private String type;
    private Long subscribersCount;
    private Instant createdAt;
}
