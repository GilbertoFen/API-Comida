package com.demoapi.apicomida.repositories;

import com.demoapi.apicomida.models.UserAccount;
import com.demoapi.apicomida.models.UserProfile;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
    Optional<UserProfile> findByUser(UserAccount user);
}
