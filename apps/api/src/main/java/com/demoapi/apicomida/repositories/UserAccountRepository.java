package com.demoapi.apicomida.repositories;

import com.demoapi.apicomida.models.UserAccount;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {
    boolean existsByEmailIgnoreCase(String email);
    Optional<UserAccount> findByEmailIgnoreCase(String email);
}
