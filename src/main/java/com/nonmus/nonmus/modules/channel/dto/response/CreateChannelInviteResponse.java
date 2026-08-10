package com.nonmus.nonmus.modules.channel.dto.response;

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
public class CreateChannelInviteResponse {
    private UUID inviteId;
    private UUID channelId;
    private String token;
    private Instant expiry;
    private Long maxUses;
    private Long currentUses;
    private String joinType;
}
