package com.nonmus.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.annotation.Generated;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String profilePicture;
    private boolean isEmailVerified;
    private Instant passwordChangedAt;
    private Instant createdAt;
    private Instant updatedAt;
}
