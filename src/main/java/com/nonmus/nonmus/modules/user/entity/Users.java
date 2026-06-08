package com.nonmus.nonmus.modules.user.entity;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.nonmus.nonmus.modules.channel.entity.Channels;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Data
public class Users implements UserDetails {
    @Id
    private UUID id = UUID.randomUUID();
    private String name;

    @Column(unique = true)
    private String email;

    @Column(name = "password_hash")
    private String password;

    private String profilePicture;

    @OneToMany(mappedBy = "user")
    private List<Channels> channels;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return email;
    }
}
