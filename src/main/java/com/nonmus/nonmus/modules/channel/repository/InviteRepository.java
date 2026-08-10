package com.nonmus.nonmus.modules.channel.repository;

import com.nonmus.nonmus.modules.channel.entity.Invite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InviteRepository extends JpaRepository<Invite, UUID> {
    boolean existsByToken(String token);

    Optional<Invite> findByToken(String token);

    @Query("SELECT i FROM Invite i WHERE i.channel.id = :channelId ORDER BY i.createdAt DESC NULLS LAST")
    List<Invite> findByChannelId(@Param("channelId") UUID channelId);
}
