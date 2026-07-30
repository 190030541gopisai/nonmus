package com.nonmus.nonmus.modules.channel.entity;

import com.nonmus.nonmus.modules.channel.enums.JoinType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
public class InviteJoinRule {
    @Id
    private UUID id = UUID.randomUUID();

    @OneToOne
    @JoinColumn(referencedColumnName = "id")
    private Invite invite;

    @Enumerated(EnumType.STRING)
    private JoinType type;

    private String passwordHash;
}
