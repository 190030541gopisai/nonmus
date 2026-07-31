package com.nonmus.nonmus.modules.channel.entity;

import com.nonmus.nonmus.modules.channel.enums.USER_ROLE;
import com.nonmus.nonmus.modules.user.entity.Users;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "channel_id"}))
public class ChannelMembers {
    @Id
    private UUID id = UUID.randomUUID();

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "channel_id", referencedColumnName = "id")
    private Channels channel;

    @Enumerated(EnumType.STRING)
    private USER_ROLE role;

    @CreatedDate
    @Column(updatable = false)
    private Instant joinedAt;

    private Instant leavedAt;
}
