package com.demoapi.apicomida.repositories;

import com.demoapi.apicomida.models.Exercise;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseRepository extends JpaRepository<Exercise, UUID> {
    List<Exercise> findByNameContainingIgnoreCaseOrderByNameAsc(String query);
    List<Exercise> findByMuscleGroupIgnoreCaseOrderByNameAsc(String muscleGroup);
}
