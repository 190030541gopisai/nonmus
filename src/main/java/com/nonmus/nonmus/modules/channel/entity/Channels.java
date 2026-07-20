package com.nonmus.nonmus.modules.channel.entity;

import java.time.Instant;
import java.util.UUID;

import com.nonmus.nonmus.modules.channel.enums.ChannelType;
import com.nonmus.nonmus.modules.user.entity.Users;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
public class Channels {
    @Id
    private UUID id = UUID.randomUUID();
    private String name;
    private String description;
    private String logo;

    @Enumerated(EnumType.STRING)
    private ChannelType type;

    private Integer subscribersCount = 0;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id", updatable = false)
    private Users createdBy;

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private Users updatedBy;

    @LastModifiedDate
    private Instant updatedAt;
}