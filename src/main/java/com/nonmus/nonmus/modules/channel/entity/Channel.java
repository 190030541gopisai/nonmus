package com.nonmus.nonmus.modules.channel.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.nonmus.nonmus.modules.channel.enums.ChannelType;
import com.nonmus.nonmus.modules.user.entity.Users;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Channel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String handle;

    @Column(nullable = false)
    private String name;

    private String description;

    private String logo;

    @Enumerated(EnumType.STRING)
    private ChannelType type;

    @ManyToOne
    @JoinColumn(name = "owner_id", updatable = false)
    private Users createdBy;

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private Users updatedBy;

    @LastModifiedDate
    private Instant updatedAt;

    @OneToOne(
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "channel_statistics_id",
            unique = true
    )
    private ChannelStatistics channelStatistics;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ChannelMember> channelMembers = new ArrayList<>();

    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Invite> invites = new ArrayList<>();

    public void addMember(ChannelMember channelMember) {
        channelMembers.add(channelMember);
        channelMember.setChannel(this);
    }

    public void removeMember(ChannelMember channelMember) {
        channelMembers.remove(channelMember);
        channelMember.setChannel(null);
    }
}