package com.nonmus.nonmus.modules.user.entity;

import java.util.List;
import java.util.UUID;

import com.nonmus.nonmus.modules.channel.entity.Channels;
import com.nonmus.nonmus.modules.subscribe.entity.Subscribers;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class Users {
    @Id
    private UUID id = UUID.randomUUID();
    private String name;

    @Column(unique = true)
    private String email;

    @Column(name = "password_hash")
    private String password;
    private String profilePictureUrl;

    @OneToMany(mappedBy = "user")
    private List<Channels> channels;

    @OneToMany(mappedBy = "user")
    private List<Subscribers> subscriptions;
}
