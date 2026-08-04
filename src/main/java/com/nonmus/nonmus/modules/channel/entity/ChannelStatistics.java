package com.nonmus.nonmus.modules.channel.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
public class ChannelStatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private Long subscribersCount;

    @OneToOne(mappedBy = "channelStatistics")
    private Channel channel;
}
