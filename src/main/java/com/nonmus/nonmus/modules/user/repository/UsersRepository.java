package com.nonmus.nonmus.modules.user.repository;

import java.util.Optional;
import java.util.UUID;

import com.nonmus.nonmus.modules.user.enums.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import com.nonmus.nonmus.modules.user.entity.Users;

public interface UsersRepository extends JpaRepository<Users, UUID> {
    Optional<Users> findByEmail(String email);
    boolean existsByEmail(String email);
}
