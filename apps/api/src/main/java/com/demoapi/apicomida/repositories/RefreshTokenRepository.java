package com.demoapi.apicomida.repositories;

import com.demoapi.apicomida.models.RefreshToken;
import com.demoapi.apicomida.models.UserAccount;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByTokenAndRevokedFalse(String token);
    List<RefreshToken> findByUserAndRevokedFalse(UserAccount user);
}
