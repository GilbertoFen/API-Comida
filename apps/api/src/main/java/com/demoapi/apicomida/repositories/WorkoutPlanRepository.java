package com.demoapi.apicomida.repositories;

import com.demoapi.apicomida.models.UserAccount;
import com.demoapi.apicomida.models.WorkoutPlan;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, UUID> {
    List<WorkoutPlan> findByUserOrderByCreatedAtDesc(UserAccount user);
}
