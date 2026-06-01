package com.demoapi.apicomida.repositories;

import com.demoapi.apicomida.models.UserAccount;
import com.demoapi.apicomida.models.UserFridgeItem;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFridgeItemRepository extends JpaRepository<UserFridgeItem, UUID> {
    List<UserFridgeItem> findByUserOrderByCreatedAtDesc(UserAccount user);
}
