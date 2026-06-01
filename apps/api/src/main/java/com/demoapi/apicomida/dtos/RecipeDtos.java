package com.demoapi.apicomida.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public final class RecipeDtos {

    private RecipeDtos() {
    }

    public record RecipeRequest(
            @NotBlank String title,
            String description,
            String instructions,
            Integer prepTimeMinutes,
            Integer cookTimeMinutes,
            Integer servings,
            boolean isPublic,
            boolean isAiGenerated,
            boolean isHighCalorie,
            boolean isHighProtein,
            boolean isLowCarb
    ) {
    }

    public record RecipeResponse(
            UUID id,
            UUID userId,
            String title,
            String description,
            String instructions,
            Integer prepTimeMinutes,
            Integer cookTimeMinutes,
            Integer servings,
            BigDecimal totalCalories,
            BigDecimal totalProteinG,
            BigDecimal totalCarbsG,
            BigDecimal totalFatG,
            boolean isPublic,
            boolean isAiGenerated,
            boolean isHighCalorie,
            boolean isHighProtein,
            boolean isLowCarb,
            List<RecipeIngredientResponse> ingredients
    ) {
    }

    public record RecipeIngredientRequest(
            @NotNull UUID foodId,
            @DecimalMin("0.0") BigDecimal quantity,
            String unit,
            BigDecimal calories,
            BigDecimal proteinG,
            BigDecimal carbsG,
            BigDecimal fatG
    ) {
    }

    public record RecipeIngredientResponse(
            UUID id,
            UUID foodId,
            String foodName,
            BigDecimal quantity,
            String unit,
            BigDecimal calories,
            BigDecimal proteinG,
            BigDecimal carbsG,
            BigDecimal fatG
    ) {
    }
}
