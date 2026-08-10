package com.nonmus.nonmus.modules.channel.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class UpdateChannelInviteRequest {
    private Instant expiry;
    private Long maxUses;
    private String joinType;
    private String password;
}