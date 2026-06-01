package com.demoapi.apicomida.repositories;

import com.demoapi.apicomida.models.UserAccount;
import com.demoapi.apicomida.models.UserFoodLog;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFoodLogRepository extends JpaRepository<UserFoodLog, UUID> {
    List<UserFoodLog> findByUserAndLogDateOrderByCreatedAtDesc(UserAccount user, LocalDate logDate);
    List<UserFoodLog> findByUserAndLogDateBetweenOrderByLogDateAscCreatedAtAsc(UserAccount user, LocalDate startDate, LocalDate endDate);
}
