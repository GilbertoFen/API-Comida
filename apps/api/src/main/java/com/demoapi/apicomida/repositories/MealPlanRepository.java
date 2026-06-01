package com.demoapi.apicomida.repositories;

import com.demoapi.apicomida.models.MealPlan;
import com.demoapi.apicomida.models.UserAccount;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MealPlanRepository extends JpaRepository<MealPlan, UUID> {
    List<MealPlan> findByUserOrderByCreatedAtDesc(UserAccount user);
}
