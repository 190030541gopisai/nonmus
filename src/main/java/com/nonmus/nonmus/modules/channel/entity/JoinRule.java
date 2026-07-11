package com.nonmus.nonmus.modules.channel.entity;
import com.nonmus.nonmus.modules.channel.enums.JoinType;

import java.util.UUID;

public class JoinRule {
    private UUID id;
    private UUID channelId;
    private JoinType type;
    private String passwordHash;
}