package com.demoapi.apicomida.services;

import com.demoapi.apicomida.dtos.ExerciseDtos.ExerciseRequest;
import com.demoapi.apicomida.dtos.ExerciseDtos.ExerciseResponse;
import com.demoapi.apicomida.exception.ApiException;
import com.demoapi.apicomida.models.Exercise;
import com.demoapi.apicomida.repositories.ExerciseRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;

    public ExerciseService(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    public List<ExerciseResponse> getAll() {
        return exerciseRepository.findAll().stream().map(this::toResponse).toList();
    }

    public ExerciseResponse getById(UUID id) {
        return toResponse(requireExercise(id));
    }

    public List<ExerciseResponse> search(String query) {
        return exerciseRepository.findByNameContainingIgnoreCaseOrderByNameAsc(query).stream().map(this::toResponse).toList();
    }

    public List<ExerciseResponse> getByMuscleGroup(String muscleGroup) {
        return exerciseRepository.findByMuscleGroupIgnoreCaseOrderByNameAsc(muscleGroup).stream().map(this::toResponse).toList();
    }

    @Transactional
    public ExerciseResponse create(ExerciseRequest request) {
        Exercise exercise = new Exercise();
        apply(exercise, request);
        return toResponse(exerciseRepository.save(exercise));
    }

    @Transactional
    public ExerciseResponse update(UUID id, ExerciseRequest request) {
        Exercise exercise = requireExercise(id);
        apply(exercise, request);
        return toResponse(exerciseRepository.save(exercise));
    }

    @Transactional
    public void delete(UUID id) {
        exerciseRepository.delete(requireExercise(id));
    }

    public Exercise requireExercise(UUID id) {
        return exerciseRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Exercise not found"));
    }

    private void apply(Exercise exercise, ExerciseRequest request) {
        exercise.setName(request.name());
        exercise.setMuscleGroup(request.muscleGroup());
        exercise.setEquipment(request.equipment());
        exercise.setDifficulty(request.difficulty());
        exercise.setDescription(request.description());
        exercise.setRecommendedSets(request.recommendedSets());
        exercise.setRecommendedReps(request.recommendedReps());
        exercise.setRecommendedTimeSeconds(request.recommendedTimeSeconds());
    }

    private ExerciseResponse toResponse(Exercise exercise) {
        return new ExerciseResponse(
                exercise.getId(),
                exercise.getName(),
                exercise.getMuscleGroup(),
                exercise.getEquipment(),
                exercise.getDifficulty(),
                exercise.getDescription(),
                exercise.getRecommendedSets(),
                exercise.getRecommendedReps(),
                exercise.getRecommendedTimeSeconds()
        );
    }
}
