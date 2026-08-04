package com.nonmus.nonmus.modules.channel.entity;

import com.nonmus.nonmus.modules.channel.enums.JoinType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
public class InviteJoinRule {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "invite_id", referencedColumnName = "id")
    private Invite invite;

    @Enumerated(EnumType.STRING)
    private JoinType type;

    private String passwordHash;
}
