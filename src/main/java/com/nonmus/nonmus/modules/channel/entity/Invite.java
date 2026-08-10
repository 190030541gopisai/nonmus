package com.nonmus.nonmus.modules.channel.entity;

import com.nonmus.nonmus.modules.user.entity.Users;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Invite {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", referencedColumnName = "id")
    private Channel channel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", referencedColumnName = "id")
    private Users user;

    @Column(unique = true)
    private String token;

    private Instant expiry;

    private Long maxUses;

    @Column(nullable = false)
    private Long currentUses = 0L;

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    @OneToOne(mappedBy = "invite", cascade = CascadeType.ALL, orphanRemoval = true)
    private InviteJoinRule inviteJoinRule;
}
