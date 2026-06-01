package com.demoapi.apicomida.services;

import com.demoapi.apicomida.dtos.WorkoutDtos.WorkoutPlanDayRequest;
import com.demoapi.apicomida.dtos.WorkoutDtos.WorkoutPlanDayResponse;
import com.demoapi.apicomida.dtos.WorkoutDtos.WorkoutPlanExerciseRequest;
import com.demoapi.apicomida.dtos.WorkoutDtos.WorkoutPlanExerciseResponse;
import com.demoapi.apicomida.dtos.WorkoutDtos.WorkoutPlanRequest;
import com.demoapi.apicomida.dtos.WorkoutDtos.WorkoutPlanResponse;
import com.demoapi.apicomida.exception.ApiException;
import com.demoapi.apicomida.models.UserAccount;
import com.demoapi.apicomida.models.WorkoutPlan;
import com.demoapi.apicomida.models.WorkoutPlanDay;
import com.demoapi.apicomida.models.WorkoutPlanExercise;
import com.demoapi.apicomida.repositories.WorkoutPlanDayRepository;
import com.demoapi.apicomida.repositories.WorkoutPlanExerciseRepository;
import com.demoapi.apicomida.repositories.WorkoutPlanRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkoutPlanService {

    private final WorkoutPlanRepository workoutPlanRepository;
    private final WorkoutPlanDayRepository workoutPlanDayRepository;
    private final WorkoutPlanExerciseRepository workoutPlanExerciseRepository;
    private final CurrentUserService currentUserService;
    private final ExerciseService exerciseService;

    public WorkoutPlanService(
            WorkoutPlanRepository workoutPlanRepository,
            WorkoutPlanDayRepository workoutPlanDayRepository,
            WorkoutPlanExerciseRepository workoutPlanExerciseRepository,
            CurrentUserService currentUserService,
            ExerciseService exerciseService
    ) {
        this.workoutPlanRepository = workoutPlanRepository;
        this.workoutPlanDayRepository = workoutPlanDayRepository;
        this.workoutPlanExerciseRepository = workoutPlanExerciseRepository;
        this.currentUserService = currentUserService;
        this.exerciseService = exerciseService;
    }

    @Transactional
    public WorkoutPlanResponse create(WorkoutPlanRequest request) {
        WorkoutPlan plan = new WorkoutPlan();
        plan.setUser(currentUserService.requireCurrentUser());
        apply(plan, request);
        return toResponse(workoutPlanRepository.save(plan));
    }

    public List<WorkoutPlanResponse> getMine() {
        return workoutPlanRepository.findByUserOrderByCreatedAtDesc(currentUserService.requireCurrentUser())
                .stream().map(this::toResponse).toList();
    }

    public WorkoutPlanResponse getById(UUID id) {
        return toResponse(requireOwnedPlan(id));
    }

    @Transactional
    public WorkoutPlanResponse update(UUID id, WorkoutPlanRequest request) {
        WorkoutPlan plan = requireOwnedPlan(id);
        apply(plan, request);
        return toResponse(workoutPlanRepository.save(plan));
    }

    @Transactional
    public void delete(UUID id) {
        workoutPlanRepository.delete(requireOwnedPlan(id));
    }

    @Transactional
    public WorkoutPlanDayResponse addDay(UUID planId, WorkoutPlanDayRequest request) {
        WorkoutPlanDay day = new WorkoutPlanDay();
        day.setWorkoutPlan(requireOwnedPlan(planId));
        day.setDayNumber(request.dayNumber());
        day.setTitle(request.title());
        day.setFocusArea(request.focusArea());
        return toDayResponse(workoutPlanDayRepository.save(day));
    }

    public List<WorkoutPlanDayResponse> getDays(UUID planId) {
        return workoutPlanDayRepository.findByWorkoutPlanOrderByDayNumberAsc(requireOwnedPlan(planId))
                .stream().map(this::toDayResponse).toList();
    }

    @Transactional
    public WorkoutPlanDayResponse updateDay(UUID dayId, WorkoutPlanDayRequest request) {
        WorkoutPlanDay day = requireOwnedDay(dayId);
        day.setDayNumber(request.dayNumber());
        day.setTitle(request.title());
        day.setFocusArea(request.focusArea());
        return toDayResponse(workoutPlanDayRepository.save(day));
    }

    @Transactional
    public void deleteDay(UUID dayId) {
        workoutPlanDayRepository.delete(requireOwnedDay(dayId));
    }

    @Transactional
    public WorkoutPlanExerciseResponse addExercise(UUID dayId, WorkoutPlanExerciseRequest request) {
        WorkoutPlanExercise exercise = new WorkoutPlanExercise();
        exercise.setWorkoutPlanDay(requireOwnedDay(dayId));
        applyExercise(exercise, request);
        return toExerciseResponse(workoutPlanExerciseRepository.save(exercise));
    }

    @Transactional
    public WorkoutPlanExerciseResponse updateExercise(UUID id, WorkoutPlanExerciseRequest request) {
        WorkoutPlanExercise exercise = requireOwnedExercise(id);
        applyExercise(exercise, request);
        return toExerciseResponse(workoutPlanExerciseRepository.save(exercise));
    }

    @Transactional
    public void deleteExercise(UUID id) {
        workoutPlanExerciseRepository.delete(requireOwnedExercise(id));
    }

    public WorkoutPlan requireOwnedPlan(UUID id) {
        UserAccount user = currentUserService.requireCurrentUser();
        return workoutPlanRepository.findById(id)
                .filter(plan -> plan.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Workout plan not found"));
    }

    private WorkoutPlanDay requireOwnedDay(UUID id) {
        UserAccount user = currentUserService.requireCurrentUser();
        return workoutPlanDayRepository.findById(id)
                .filter(day -> day.getWorkoutPlan().getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Workout plan day not found"));
    }

    private WorkoutPlanExercise requireOwnedExercise(UUID id) {
        UserAccount user = currentUserService.requireCurrentUser();
        return workoutPlanExerciseRepository.findById(id)
                .filter(exercise -> exercise.getWorkoutPlanDay().getWorkoutPlan().getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Workout plan exercise not found"));
    }

    private void apply(WorkoutPlan plan, WorkoutPlanRequest request) {
        plan.setTitle(request.title());
        plan.setDescription(request.description());
        plan.setGoal(request.goal());
        plan.setDaysPerWeek(request.daysPerWeek());
        plan.setAiGenerated(request.isAiGenerated());
    }

    private void applyExercise(WorkoutPlanExercise exercise, WorkoutPlanExerciseRequest request) {
        exercise.setExercise(exerciseService.requireExercise(request.exerciseId()));
        exercise.setSets(request.sets());
        exercise.setReps(request.reps());
        exercise.setWeightKg(request.weightKg());
        exercise.setDurationSeconds(request.durationSeconds());
        exercise.setRestSeconds(request.restSeconds());
        exercise.setNotes(request.notes());
    }

    private WorkoutPlanResponse toResponse(WorkoutPlan plan) {
        return new WorkoutPlanResponse(
                plan.getId(),
                plan.getTitle(),
                plan.getDescription(),
                plan.getGoal(),
                plan.getDaysPerWeek(),
                plan.isAiGenerated()
        );
    }

    private WorkoutPlanDayResponse toDayResponse(WorkoutPlanDay day) {
        return new WorkoutPlanDayResponse(
                day.getId(),
                day.getDayNumber(),
                day.getTitle(),
                day.getFocusArea(),
                workoutPlanExerciseRepository.findByWorkoutPlanDay(day).stream().map(this::toExerciseResponse).toList()
        );
    }

    private WorkoutPlanExerciseResponse toExerciseResponse(WorkoutPlanExercise exercise) {
        return new WorkoutPlanExerciseResponse(
                exercise.getId(),
                exercise.getExercise().getId(),
                exercise.getExercise().getName(),
                exercise.getSets(),
                exercise.getReps(),
                exercise.getWeightKg(),
                exercise.getDurationSeconds(),
                exercise.getRestSeconds(),
                exercise.getNotes()
        );
    }
}
