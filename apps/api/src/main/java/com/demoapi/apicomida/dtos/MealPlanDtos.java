package com.demoapi.apicomida.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public final class MealPlanDtos {

    private MealPlanDtos() {
    }

    public record MealPlanRequest(
            @NotBlank String title,
            String description,
            LocalDate startDate,
            LocalDate endDate,
            @DecimalMin("0.0") BigDecimal dailyCalorieTarget,
            boolean isAiGenerated
    ) {
    }

    public record MealPlanResponse(
            UUID id,
            String title,
            String description,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal dailyCalorieTarget,
            boolean isAiGenerated,
            boolean hasExcessCalories,
            boolean hasLowProtein,
            boolean hasLowCalories
    ) {
    }

    public record MealPlanDayRequest(
            Integer dayNumber,
            LocalDate dayDate,
            BigDecimal totalCalories,
            BigDecimal totalProteinG,
            BigDecimal totalCarbsG,
            BigDecimal totalFatG
    ) {
    }

    public record MealPlanDayResponse(
            UUID id,
            Integer dayNumber,
            LocalDate dayDate,
            BigDecimal totalCalories,
            BigDecimal totalProteinG,
            BigDecimal totalCarbsG,
            BigDecimal totalFatG,
            List<MealPlanMealResponse> meals
    ) {
    }

    public record MealPlanMealRequest(
            String mealType,
            UUID recipeId,
            UUID foodId,
            BigDecimal quantity,
            String notes,
            BigDecimal calories,
            BigDecimal proteinG,
            BigDecimal carbsG,
            BigDecimal fatG
    ) {
    }

    public record MealPlanMealResponse(
            UUID id,
            String mealType,
            UUID recipeId,
            UUID foodId,
            BigDecimal quantity,
            String notes,
            BigDecimal calories,
            BigDecimal proteinG,
            BigDecimal carbsG,
            BigDecimal fatG
    ) {
    }

    public record MealPlanAnalysisResponse(
            boolean hasExcessCalories,
            boolean hasLowCalories,
            boolean hasLowProtein,
            boolean matchesUserGoal,
            BigDecimal totalCalories,
            BigDecimal totalProteinG
    ) {
    }
}
