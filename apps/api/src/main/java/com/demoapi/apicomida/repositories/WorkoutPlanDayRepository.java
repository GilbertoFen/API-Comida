package com.demoapi.apicomida.repositories;

import com.demoapi.apicomida.models.WorkoutPlan;
import com.demoapi.apicomida.models.WorkoutPlanDay;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutPlanDayRepository extends JpaRepository<WorkoutPlanDay, UUID> {
    List<WorkoutPlanDay> findByWorkoutPlanOrderByDayNumberAsc(WorkoutPlan workoutPlan);
}
