package com.demoapi.apicomida.services;

import com.demoapi.apicomida.dtos.WorkoutDtos.WorkoutLogExerciseRequest;
import com.demoapi.apicomida.dtos.WorkoutDtos.WorkoutLogExerciseResponse;
import com.demoapi.apicomida.dtos.WorkoutDtos.WorkoutLogRequest;
import com.demoapi.apicomida.dtos.WorkoutDtos.WorkoutLogResponse;
import com.demoapi.apicomida.dtos.WorkoutDtos.WorkoutLogSummaryResponse;
import com.demoapi.apicomida.exception.ApiException;
import com.demoapi.apicomida.models.UserAccount;
import com.demoapi.apicomida.models.WorkoutLog;
import com.demoapi.apicomida.models.WorkoutLogExercise;
import com.demoapi.apicomida.repositories.WorkoutLogExerciseRepository;
import com.demoapi.apicomida.repositories.WorkoutLogRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkoutLogService {

    private final WorkoutLogRepository workoutLogRepository;
    private final WorkoutLogExerciseRepository workoutLogExerciseRepository;
    private final CurrentUserService currentUserService;
    private final WorkoutPlanService workoutPlanService;
    private final ExerciseService exerciseService;

    public WorkoutLogService(
            WorkoutLogRepository workoutLogRepository,
            WorkoutLogExerciseRepository workoutLogExerciseRepository,
            CurrentUserService currentUserService,
            WorkoutPlanService workoutPlanService,
            ExerciseService exerciseService
    ) {
        this.workoutLogRepository = workoutLogRepository;
        this.workoutLogExerciseRepository = workoutLogExerciseRepository;
        this.currentUserService = currentUserService;
        this.workoutPlanService = workoutPlanService;
        this.exerciseService = exerciseService;
    }

    @Transactional
    public WorkoutLogResponse create(WorkoutLogRequest request) {
        WorkoutLog log = new WorkoutLog();
        log.setUser(currentUserService.requireCurrentUser());
        apply(log, request);
        return toResponse(workoutLogRepository.save(log));
    }

    public List<WorkoutLogResponse> getByDate(LocalDate date) {
        return workoutLogRepository.findByUserAndLogDateOrderByCreatedAtDesc(currentUserService.requireCurrentUser(), date)
                .stream().map(this::toResponse).toList();
    }

    public List<WorkoutLogResponse> getByRange(LocalDate startDate, LocalDate endDate) {
        return workoutLogRepository.findByUserAndLogDateBetweenOrderByLogDateAscCreatedAtAsc(
                currentUserService.requireCurrentUser(), startDate, endDate
        ).stream().map(this::toResponse).toList();
    }

    public WorkoutLogSummaryResponse getSummary() {
        List<WorkoutLogResponse> logs = getByRange(LocalDate.of(2000, 1, 1), LocalDate.of(2999, 1, 1));
        int duration = logs.stream().map(WorkoutLogResponse::totalDurationMinutes).filter(java.util.Objects::nonNull).mapToInt(Integer::intValue).sum();
        double avg = logs.stream().map(WorkoutLogResponse::userRating).filter(java.util.Objects::nonNull).mapToInt(Integer::intValue).average().orElse(0);
        return new WorkoutLogSummaryResponse((long) logs.size(), duration, avg);
    }

    public WorkoutLogResponse getById(UUID id) {
        return toResponse(requireOwnedLog(id));
    }

    @Transactional
    public WorkoutLogResponse update(UUID id, WorkoutLogRequest request) {
        WorkoutLog log = requireOwnedLog(id);
        apply(log, request);
        return toResponse(workoutLogRepository.save(log));
    }

    @Transactional
    public void delete(UUID id) {
        workoutLogRepository.delete(requireOwnedLog(id));
    }

    @Transactional
    public WorkoutLogExerciseResponse addExercise(UUID logId, WorkoutLogExerciseRequest request) {
        WorkoutLogExercise exercise = new WorkoutLogExercise();
        exercise.setWorkoutLog(requireOwnedLog(logId));
        applyExercise(exercise, request);
        return toExerciseResponse(workoutLogExerciseRepository.save(exercise));
    }

    @Transactional
    public WorkoutLogExerciseResponse updateExercise(UUID id, WorkoutLogExerciseRequest request) {
        WorkoutLogExercise exercise = requireOwnedExercise(id);
        applyExercise(exercise, request);
        return toExerciseResponse(workoutLogExerciseRepository.save(exercise));
    }

    @Transactional
    public void deleteExercise(UUID id) {
        workoutLogExerciseRepository.delete(requireOwnedExercise(id));
    }

    private void apply(WorkoutLog log, WorkoutLogRequest request) {
        log.setWorkoutPlan(request.workoutPlanId() != null ? workoutPlanService.requireOwnedPlan(request.workoutPlanId()) : null);
        log.setLogDate(request.logDate());
        log.setTotalDurationMinutes(request.totalDurationMinutes());
        log.setUserRating(request.userRating());
        log.setAiRating(request.aiRating());
        log.setNotes(request.notes());
    }

    private void applyExercise(WorkoutLogExercise exercise, WorkoutLogExerciseRequest request) {
        exercise.setExercise(exerciseService.requireExercise(request.exerciseId()));
        exercise.setSetsDone(request.setsDone());
        exercise.setRepsDone(request.repsDone());
        exercise.setWeightKg(request.weightKg());
        exercise.setDurationSeconds(request.durationSeconds());
        exercise.setAiRecommendedDurationSeconds(request.aiRecommendedDurationSeconds());
        exercise.setAiScore(request.aiScore());
        exercise.setUserNotes(request.userNotes());
    }

    private WorkoutLog requireOwnedLog(UUID id) {
        UserAccount user = currentUserService.requireCurrentUser();
        return workoutLogRepository.findById(id)
                .filter(log -> log.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Workout log not found"));
    }

    private WorkoutLogExercise requireOwnedExercise(UUID id) {
        UserAccount user = currentUserService.requireCurrentUser();
        return workoutLogExerciseRepository.findById(id)
                .filter(exercise -> exercise.getWorkoutLog().getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Workout log exercise not found"));
    }

    private WorkoutLogResponse toResponse(WorkoutLog log) {
        return new WorkoutLogResponse(
                log.getId(),
                log.getWorkoutPlan() != null ? log.getWorkoutPlan().getId() : null,
                log.getLogDate(),
                log.getTotalDurationMinutes(),
                log.getUserRating(),
                log.getAiRating(),
                log.getNotes(),
                workoutLogExerciseRepository.findByWorkoutLog(log).stream().map(this::toExerciseResponse).toList()
        );
    }

    private WorkoutLogExerciseResponse toExerciseResponse(WorkoutLogExercise exercise) {
        return new WorkoutLogExerciseResponse(
                exercise.getId(),
                exercise.getExercise().getId(),
                exercise.getExercise().getName(),
                exercise.getSetsDone(),
                exercise.getRepsDone(),
                exercise.getWeightKg(),
                exercise.getDurationSeconds(),
                exercise.getAiRecommendedDurationSeconds(),
                exercise.getAiScore(),
                exercise.getUserNotes()
        );
    }
}
