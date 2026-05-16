package com.nonmus.nonmus.modules.videos.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;

import com.nonmus.nonmus.modules.channel.entity.Channels;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

enum Visibility {
    PUBLIC,
    PRIVATE,
    UNLISTED
}

@Entity
@Data
public class Videos {
    @Id
    private UUID id = UUID.randomUUID();

    private String videoUrl;
    private String thumbnailUrl;
    
    private String title;
    private String description;
    private Integer duration; // Duration in seconds

    @Enumerated(EnumType.STRING)
    private Visibility visibility; 

    @CreatedDate
    private LocalDateTime uploadedDate;

    @ManyToOne
    @JoinColumn(name = "channel_id")
    private Channels channel;
}
