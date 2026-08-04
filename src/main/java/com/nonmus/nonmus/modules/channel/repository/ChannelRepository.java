package com.nonmus.nonmus.modules.channel.repository;

import com.nonmus.nonmus.modules.channel.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, UUID> {
    boolean existsByHandleIgnoreCase(String handle);

    Optional<Channel> findByHandle(String handle);

    boolean existsByHandle(String handle);
}
