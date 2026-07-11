package com.nonmus.nonmus.modules.channel.repository;

import com.nonmus.nonmus.modules.channel.entity.Channels;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChannelsRepository extends JpaRepository<Channels, UUID> {
}
