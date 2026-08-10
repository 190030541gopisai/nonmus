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
public class InviteResponse {
    private UUID inviteId;
    private UUID channelId;
    private String channelName;
    private String channelLogo;
    private String joinType;
    private Instant expiry;
    private Long currentUses;
    private Long maxUses;
}