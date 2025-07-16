package com.local.core.user.repository;

import com.local.core.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<User, UUID>, UserRepoExtension {
    Optional<User> findByKeycloakUserId(String keycloakUserId);
}
