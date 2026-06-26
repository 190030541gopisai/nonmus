package com.nonmus.nonmus.modules.channel.entity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.nonmus.nonmus.modules.user.entity.Users;
import com.nonmus.nonmus.modules.videos.entity.Videos;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Data
public class Channels {
    @Id
    private UUID id = UUID.randomUUID();
    private String name;
    private String description;
    private String logo;
    private String joinLink;
    private ChannelType type;

    @CreatedDate
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Users user;

    @OneToMany(mappedBy = "channel")
    private List<Videos> videos;
}


enum ChannelType {
    PRIVATE,
    PUBLIC
}