package com.nonmus.nonmus.modules.channel.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
public class Invite {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", referencedColumnName = "id")
    private Channel channel;

    @Column(unique = true)
    private String token;

    private Instant expiry;

    private Long maxUses;

    @Column(nullable = false)
    private Long currentUses = 0L;

    @OneToOne(mappedBy = "invite", cascade = CascadeType.ALL, orphanRemoval = true)
    private InviteJoinRule inviteJoinRule;
}
