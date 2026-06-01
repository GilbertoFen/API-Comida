package com.demoapi.apicomida.repositories;

import com.demoapi.apicomida.models.AiRequest;
import com.demoapi.apicomida.models.UserAccount;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiRequestRepository extends JpaRepository<AiRequest, UUID> {
    List<AiRequest> findByUserOrderByCreatedAtDesc(UserAccount user);
}
