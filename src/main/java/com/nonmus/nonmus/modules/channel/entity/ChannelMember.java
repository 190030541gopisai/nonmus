package com.nonmus.nonmus.modules.channel.entity;

import com.nonmus.nonmus.modules.channel.enums.ChannelRole;
import com.nonmus.nonmus.modules.user.entity.Users;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(indexes = {
        @Index(name = "idx_channel_member_user_left_joined", columnList = "user_id, left_at, joined_at, id")
})
@EntityListeners(AuditingEntityListener.class)
public class ChannelMember {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable=false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @Enumerated(EnumType.STRING)
    private ChannelRole role;

    @CreatedDate
    @Column(updatable = false)
    private Instant joinedAt;

    private Instant leftAt;
}
