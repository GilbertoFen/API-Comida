package com.demoapi.apicomida.repositories;

import com.demoapi.apicomida.models.UserAccount;
import com.demoapi.apicomida.models.WorkoutLog;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutLogRepository extends JpaRepository<WorkoutLog, UUID> {
    List<WorkoutLog> findByUserAndLogDateOrderByCreatedAtDesc(UserAccount user, LocalDate logDate);
    List<WorkoutLog> findByUserAndLogDateBetweenOrderByLogDateAscCreatedAtAsc(UserAccount user, LocalDate startDate, LocalDate endDate);
}
