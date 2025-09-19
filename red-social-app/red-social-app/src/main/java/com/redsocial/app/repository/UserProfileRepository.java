package com.redsocial.app.repository;

import com.redsocial.app.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {

    Optional<UserProfile> findByAlias(String alias);

    Optional<UserProfile> findByUser_Id(UUID userId);

    boolean existsByAlias(String alias);
}
