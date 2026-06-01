package com.demoapi.apicomida.repositories;

import com.demoapi.apicomida.models.WorkoutLog;
import com.demoapi.apicomida.models.WorkoutLogExercise;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutLogExerciseRepository extends JpaRepository<WorkoutLogExercise, UUID> {
    List<WorkoutLogExercise> findByWorkoutLog(WorkoutLog workoutLog);
}
