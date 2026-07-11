package com.nonmus.nonmus.modules.channel.entity;

import java.time.Instant;
import java.util.UUID;

public class Invite {
    private UUID id;
    private UUID channelId;
    private String shortUrl;
    private Instant createdAt;
    private UUID createdBy;
}
