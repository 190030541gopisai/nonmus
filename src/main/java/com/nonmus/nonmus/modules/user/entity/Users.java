package com.nonmus.nonmus.modules.user.entity;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.nonmus.nonmus.modules.user.enums.Provider;
import jakarta.persistence.*;
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
public class Users implements UserDetails {
    @Id
    private UUID id = UUID.randomUUID();
    private String name;

    @Column
    private String email;

    @Column(name = "password_hash", nullable = true)
    private String password;

    private String profilePicture;

    @Enumerated(EnumType.STRING)
    private Provider profilePictureProvider;

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return email;
    }
}
