package com.demoapi.apicomida.repositories;

import com.demoapi.apicomida.models.MealPlan;
import com.demoapi.apicomida.models.MealPlanDay;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MealPlanDayRepository extends JpaRepository<MealPlanDay, UUID> {
    List<MealPlanDay> findByMealPlanOrderByDayNumberAsc(MealPlan mealPlan);
}
