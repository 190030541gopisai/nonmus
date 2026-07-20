package com.nonmus.nonmus.modules.auth.repository;

import com.nonmus.nonmus.modules.auth.entity.EmailVerificationToken;
import com.nonmus.nonmus.modules.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmailVerificationTokenRepository
        extends JpaRepository<EmailVerificationToken, UUID> {

    Optional<EmailVerificationToken> findByToken(String token);

    void deleteByUser(Users user);

    Optional<EmailVerificationToken> findByUser(Users user);

    void deleteAllByExpiresAtBefore(LocalDateTime time);
}
