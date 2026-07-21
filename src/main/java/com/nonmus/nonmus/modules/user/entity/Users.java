package com.nonmus.nonmus.modules.user.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.nonmus.nonmus.modules.user.enums.Provider;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Users {
    @Id
    private UUID id = UUID.randomUUID();
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private Boolean emailVerified = false;

    @Column(name = "password_hash", nullable = true)
    private String password;

    private String profilePicture = "users/default-avatar.png";

    @Enumerated(EnumType.STRING)
    private Provider profilePictureProvider;

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Providers> providers = new ArrayList<>();

    public void addProvider(Provider providerType) {
        Providers provider = new Providers();
        provider.setProvider(providerType);
        provider.setUser(this);
        providers.add(provider);
    }
}
