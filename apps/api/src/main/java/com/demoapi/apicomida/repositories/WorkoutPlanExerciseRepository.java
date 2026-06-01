package com.demoapi.apicomida.repositories;

import com.demoapi.apicomida.models.WorkoutPlanDay;
import com.demoapi.apicomida.models.WorkoutPlanExercise;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutPlanExerciseRepository extends JpaRepository<WorkoutPlanExercise, UUID> {
    List<WorkoutPlanExercise> findByWorkoutPlanDay(WorkoutPlanDay workoutPlanDay);
}
