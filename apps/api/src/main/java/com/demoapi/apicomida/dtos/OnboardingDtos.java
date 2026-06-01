package com.demoapi.apicomida.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public final class OnboardingDtos {

    private OnboardingDtos() {
    }

    public record QuestionnaireRequest(
            String mainGoal,
            @DecimalMin("0.0") BigDecimal currentWeightKg,
            @DecimalMin("0.0") BigDecimal targetWeightKg,
            @DecimalMin("0.0") BigDecimal heightCm,
            @Min(0) Integer age,
            String gender,
            String activityLevel,
            Integer trainingDaysPerWeek,
            String dietType,
            String foodRestrictions,
            String allergies,
            String preferredFoods,
            String dislikedFoods,
            String healthNotes
    ) {
    }

    public record QuestionnaireResponse(
            UUID id,
            String mainGoal,
            BigDecimal currentWeightKg,
            BigDecimal targetWeightKg,
            BigDecimal heightCm,
            Integer age,
            String gender,
            String activityLevel,
            Integer trainingDaysPerWeek,
            String dietType,
            String foodRestrictions,
            String allergies,
            String preferredFoods,
            String dislikedFoods,
            String healthNotes,
            LocalDateTime completedAt
    ) {
    }

    public record GoalCalculationRequest(
            @DecimalMin("0.0") BigDecimal weightKg,
            @DecimalMin("0.0") BigDecimal targetWeightKg,
            @DecimalMin("0.0") BigDecimal heightCm,
            @Min(0) Integer age,
            String gender,
            String activityLevel,
            String mainGoal
    ) {
    }

    public record GoalCalculationResponse(
            BigDecimal imc,
            BigDecimal recommendedCalories,
            BigDecimal recommendedProteinG,
            BigDecimal recommendedCarbsG,
            BigDecimal recommendedFatG,
            String suggestedGoal
    ) {
    }
}
