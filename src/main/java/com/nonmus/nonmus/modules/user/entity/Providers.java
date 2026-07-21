package com.nonmus.nonmus.modules.user.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.nonmus.nonmus.modules.user.enums.Provider;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class Providers {
    @Id
    private UUID id = UUID.randomUUID();

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonBackReference
    private Users user;

    @Enumerated(EnumType.STRING)
    private Provider provider;
}
