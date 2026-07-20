package com.nonmus.nonmus.modules.channel.entity;

import com.nonmus.nonmus.modules.channel.enums.USER_ROLE;

import java.time.Instant;
import java.util.UUID;

public class ChannelMembers {
    private UUID channelId;
    private UUID userId;
    private USER_ROLE role;
    private Instant joinedAt;
    private Instant leavedAt;
}

