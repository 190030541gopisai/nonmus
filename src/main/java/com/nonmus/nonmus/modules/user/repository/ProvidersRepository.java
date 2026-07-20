package com.nonmus.nonmus.modules.user.repository;


import com.nonmus.nonmus.modules.user.entity.Providers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProvidersRepository extends JpaRepository<Providers, UUID> {
}
