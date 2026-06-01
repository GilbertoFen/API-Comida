package com.demoapi.apicomida.repositories;

import com.demoapi.apicomida.models.MealPlanDay;
import com.demoapi.apicomida.models.MealPlanMeal;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MealPlanMealRepository extends JpaRepository<MealPlanMeal, UUID> {
    List<MealPlanMeal> findByMealPlanDay(MealPlanDay mealPlanDay);
}
