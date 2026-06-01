package com.demoapi.apicomida.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public final class FoodLogDtos {

    private FoodLogDtos() {
    }

    public record FoodLogRequest(
            @NotNull UUID foodId,
            @NotNull LocalDate logDate,
            @NotBlank String mealType,
            @DecimalMin("0.0") BigDecimal quantity,
            String unit,
            BigDecimal calories,
            BigDecimal proteinG,
            BigDecimal carbsG,
            BigDecimal fatG
    ) {
    }

    public record FoodLogResponse(
            UUID id,
            UUID foodId,
            String foodName,
            LocalDate logDate,
            String mealType,
            BigDecimal quantity,
            String unit,
            BigDecimal calories,
            BigDecimal proteinG,
            BigDecimal carbsG,
            BigDecimal fatG
    ) {
    }

    public record FoodLogSummaryResponse(
            LocalDate date,
            BigDecimal totalCalories,
            BigDecimal totalProteinG,
            BigDecimal totalCarbsG,
            BigDecimal totalFatG,
            List<FoodLogResponse> items
    ) {
    }
}
