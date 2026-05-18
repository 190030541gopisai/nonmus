package com.nonmus.nonmus.modules.channel.entity;

import java.util.List;
import java.util.UUID;

import com.nonmus.nonmus.modules.subscribe.entity.Subscribers;
import com.nonmus.nonmus.modules.user.entity.Users;
import com.nonmus.nonmus.modules.videos.entity.Videos;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class Channels {
    @Id
    private UUID id = UUID.randomUUID();
    private String name;
    private String description;
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Users user;

    @OneToMany(mappedBy = "channel")
    private List<Videos> videos;

    @OneToMany(mappedBy = "channel")
    private List<Subscribers> subscribers;
}
