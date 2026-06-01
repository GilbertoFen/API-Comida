package com.demoapi.apicomida.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public final class FridgeDtos {

    private FridgeDtos() {
    }

    public record FridgeItemRequest(
            @NotNull UUID foodId,
            @DecimalMin("0.0") BigDecimal quantity,
            String unit,
            LocalDate expirationDate
    ) {
    }

    public record FridgeItemResponse(
            UUID id,
            UUID foodId,
            String foodName,
            BigDecimal quantity,
            String unit,
            LocalDate expirationDate
    ) {
    }

    public record RecipeMatchResponse(
            UUID recipeId,
            String recipeTitle,
            int matchedIngredients,
            int totalIngredients,
            List<String> matchedFoodNames
    ) {
    }
}
