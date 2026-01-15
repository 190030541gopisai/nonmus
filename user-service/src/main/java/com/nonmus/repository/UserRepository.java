package com.nonmus.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nonmus.entity.User;

public interface UserRepository extends JpaRepository<User, UUID> {
    public Optional<User> findByEmail(String email);
}
