package com.nonmus.nonmus.modules.subscribe.entity;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;

import com.nonmus.nonmus.modules.channel.entity.Channels;
import com.nonmus.nonmus.modules.user.entity.Users;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.Data;

@Entity
@Data
public class Subscribers {
    @Id
    private UUID id  = UUID.randomUUID();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "channel_id")
    private Channels channel;

    @CreatedDate
    private LocalDate subscribedDate;
}
