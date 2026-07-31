package com.nonmus.nonmus.modules.channel.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
public class Invite {

    @Id
    private UUID id = UUID.randomUUID();

    @ManyToOne
    @JoinColumn(name = "channel_id", referencedColumnName = "id")
    private Channels channel;

    private String token;

    private Instant expiry;

    @OneToOne(mappedBy = "invite", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private InviteJoinRule inviteJoinRule;
}
