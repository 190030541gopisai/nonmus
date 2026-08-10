package com.nonmus.nonmus.modules.channel.repository;

import com.nonmus.nonmus.modules.channel.entity.ChannelMember;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChannelMemberRepository extends JpaRepository<ChannelMember, UUID> {
    boolean existsByChannelIdAndUserIdAndLeftAtIsNull(UUID channelId, UUID userId);

    Optional<ChannelMember> findByChannelIdAndUserIdAndLeftAtIsNull(UUID channelId, UUID userId);

    @EntityGraph(attributePaths = {"channel", "channel.channelStatistics"})
    List<ChannelMember> findAllByUserIdAndLeftAtIsNull(UUID userId);

    @Query("""
            SELECT m FROM ChannelMember m
            JOIN FETCH m.channel c
            JOIN FETCH c.channelStatistics
            WHERE m.user.id = :userId
              AND m.leftAt IS NULL
            ORDER BY m.joinedAt DESC, m.id DESC
            """)
    List<ChannelMember> findSubscribed(
            @Param("userId") UUID userId,
            Pageable pageable);

    @Query("""
            SELECT m FROM ChannelMember m
            JOIN FETCH m.channel c
            JOIN FETCH c.channelStatistics
            WHERE m.user.id = :userId
              AND m.leftAt IS NULL
              AND (m.joinedAt < :cursorTime
                  OR (m.joinedAt = :cursorTime AND m.id < :cursorId))
            ORDER BY m.joinedAt DESC, m.id DESC
            """)
    List<ChannelMember> findSubscribedAfter(
            @Param("userId") UUID userId,
            @Param("cursorTime") Instant cursorTime,
            @Param("cursorId") UUID cursorId,
            Pageable pageable);
}
