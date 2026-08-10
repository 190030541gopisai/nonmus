package com.nonmus.nonmus.modules.channel.dto.internal;

import com.nonmus.nonmus.modules.channel.entity.Invite;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateChannelInviteResult {
    private Invite invite;
}
