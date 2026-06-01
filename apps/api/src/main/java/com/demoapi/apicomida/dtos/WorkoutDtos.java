package com.demoapi.apicomida.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public final class WorkoutDtos {

    private WorkoutDtos() {
    }

    public record WorkoutPlanRequest(
            String title,
            String description,
            String goal,
            Integer daysPerWeek,
            boolean isAiGenerated
    ) {
    }

    public record WorkoutPlanResponse(
            UUID id,
            String title,
            String description,
            String goal,
            Integer daysPerWeek,
            boolean isAiGenerated
    ) {
    }

    public record WorkoutPlanDayRequest(
            Integer dayNumber,
            String title,
            String focusArea
    ) {
    }

    public record WorkoutPlanDayResponse(
            UUID id,
            Integer dayNumber,
            String title,
            String focusArea,
            List<WorkoutPlanExerciseResponse> exercises
    ) {
    }

    public record WorkoutPlanExerciseRequest(
            UUID exerciseId,
            Integer sets,
            String reps,
            BigDecimal weightKg,
            Integer durationSeconds,
            Integer restSeconds,
            String notes
    ) {
    }

    public record WorkoutPlanExerciseResponse(
            UUID id,
            UUID exerciseId,
            String exerciseName,
            Integer sets,
            String reps,
            BigDecimal weightKg,
            Integer durationSeconds,
            Integer restSeconds,
            String notes
    ) {
    }

    public record WorkoutLogRequest(
            UUID workoutPlanId,
            LocalDate logDate,
            Integer totalDurationMinutes,
            Integer userRating,
            Integer aiRating,
            String notes
    ) {
    }

    public record WorkoutLogResponse(
            UUID id,
            UUID workoutPlanId,
            LocalDate logDate,
            Integer totalDurationMinutes,
            Integer userRating,
            Integer aiRating,
            String notes,
            List<WorkoutLogExerciseResponse> exercises
    ) {
    }

    public record WorkoutLogExerciseRequest(
            UUID exerciseId,
            Integer setsDone,
            String repsDone,
            BigDecimal weightKg,
            Integer durationSeconds,
            Integer aiRecommendedDurationSeconds,
            Integer aiScore,
            String userNotes
    ) {
    }

    public record WorkoutLogExerciseResponse(
            UUID id,
            UUID exerciseId,
            String exerciseName,
            Integer setsDone,
            String repsDone,
            BigDecimal weightKg,
            Integer durationSeconds,
            Integer aiRecommendedDurationSeconds,
            Integer aiScore,
            String userNotes
    ) {
    }

    public record WorkoutLogSummaryResponse(
            long totalLogs,
            Integer totalDurationMinutes,
            Double averageUserRating
    ) {
    }
}
