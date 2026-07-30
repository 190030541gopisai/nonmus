package com.nonmus.nonmus.modules.channel.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
public class ChannelStatistics {
    @Id
    private UUID id = UUID.randomUUID();
    private Long subscribersCount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="channel_id", referencedColumnName = "id")
    private Channels channel;
}
