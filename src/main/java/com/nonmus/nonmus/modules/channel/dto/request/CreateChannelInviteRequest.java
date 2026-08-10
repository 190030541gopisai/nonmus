package com.nonmus.nonmus.modules.channel.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class CreateChannelInviteRequest {
    private UUID channelId;
    private Instant expiry;
    private Long maxUses;
    private String joinType;
    private String password;
}
