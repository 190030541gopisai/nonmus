package com.nonmus.nonmus.modules.channel.dto.response;

import com.nonmus.nonmus.modules.channel.enums.ChannelRole;
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
public class JoinChannelResponse {
    private UUID channelId;
    private String channelName;
    private String channelLogo;
    private ChannelRole role;
    private Instant joinedAt;
}
