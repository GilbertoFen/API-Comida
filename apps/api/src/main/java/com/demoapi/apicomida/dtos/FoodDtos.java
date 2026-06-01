package com.demoapi.apicomida.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.UUID;

public final class FoodDtos {

    private FoodDtos() {
    }

    public record FoodRequest(
            @NotBlank String name,
            String brand,
            @NotBlank String category,
            @DecimalMin("0.0") BigDecimal servingSize,
            String servingUnit,
            @DecimalMin("0.0") BigDecimal caloriesPer100g,
            @DecimalMin("0.0") BigDecimal proteinPer100g,
            @DecimalMin("0.0") BigDecimal carbsPer100g,
            @DecimalMin("0.0") BigDecimal fatPer100g,
            @DecimalMin("0.0") BigDecimal fiberPer100g,
            @DecimalMin("0.0") BigDecimal sugarPer100g,
            @DecimalMin("0.0") BigDecimal sodiumPer100g,
            String barcode,
            String externalSource
    ) {
    }

    public record CustomFoodRequest(
            @NotBlank String name,
            String brand,
            String category,
            @DecimalMin("0.0") BigDecimal servingSize,
            String servingUnit,
            @DecimalMin("0.0") BigDecimal caloriesPer100g,
            @DecimalMin("0.0") BigDecimal proteinPer100g,
            @DecimalMin("0.0") BigDecimal carbsPer100g,
            @DecimalMin("0.0") BigDecimal fatPer100g,
            @DecimalMin("0.0") BigDecimal fiberPer100g,
            @DecimalMin("0.0") BigDecimal sugarPer100g,
            @DecimalMin("0.0") BigDecimal sodiumPer100g
    ) {
    }

    public record FoodResponse(
            UUID id,
            String name,
            String brand,
            String barcode,
            String externalSource,
            String externalId,
            String category,
            BigDecimal servingSize,
            String servingUnit,
            BigDecimal caloriesPer100g,
            BigDecimal proteinPer100g,
            BigDecimal carbsPer100g,
            BigDecimal fatPer100g,
            BigDecimal fiberPer100g,
            BigDecimal sugarPer100g,
            BigDecimal sodiumPer100g,
            Boolean verified
    ) {
    }
}
