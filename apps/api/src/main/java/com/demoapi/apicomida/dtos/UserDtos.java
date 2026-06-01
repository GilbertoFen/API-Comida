package com.demoapi.apicomida.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.UUID;

public final class UserDtos {

    private UserDtos() {
    }

    public record UserResponse(
            UUID id,
            String email,
            boolean isActive,
            ProfileResponse profile
    ) {
    }

    public record ProfileResponse(
            String firstName,
            String lastName,
            Integer age,
            String gender,
            BigDecimal weightKg,
            BigDecimal heightCm,
            String activityLevel,
            String goal,
            BigDecimal imc,
            BigDecimal targetWeightKg,
            BigDecimal dailyCalorieGoal,
            BigDecimal dailyProteinGoal,
            BigDecimal dailyCarbsGoal,
            BigDecimal dailyFatGoal
    ) {
    }

    public record UpdateProfileRequest(
            String firstName,
            String lastName,
            @Min(0) Integer age,
            String gender,
            @DecimalMin("0.0") BigDecimal weightKg,
            @DecimalMin("0.0") BigDecimal heightCm,
            String activityLevel,
            String goal,
            @DecimalMin("0.0") BigDecimal targetWeightKg,
            @DecimalMin("0.0") BigDecimal dailyCalorieGoal,
            @DecimalMin("0.0") BigDecimal dailyProteinGoal,
            @DecimalMin("0.0") BigDecimal dailyCarbsGoal,
            @DecimalMin("0.0") BigDecimal dailyFatGoal
    ) {
    }

    public record UserStatsResponse(
            long totalFoodLogs,
            long totalRecipes,
            long totalMealPlans,
            long totalWorkoutPlans,
            long totalWorkoutLogs,
            long totalDailyNotes
    ) {
    }
}
