package com.demoapi.apicomida.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import java.math.BigDecimal;
import java.util.List;

public final class OpenFoodFactsDtos {

    private OpenFoodFactsDtos() {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ProductByBarcodeResponse(
            Integer status,
            String code,
            OpenFoodFactsProductResponse product
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SearchResponse(
            @JsonProperty("products") List<OpenFoodFactsProductResponse> products
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record OpenFoodFactsProductResponse(
            String code,
            @JsonProperty("product_name") String productName,
            String brands,
            String categories,
            OpenFoodFactsNutrimentsResponse nutriments,
            JsonNode rawData
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record OpenFoodFactsNutrimentsResponse(
            @JsonProperty("energy-kcal_100g") BigDecimal energyKcal100g,
            @JsonProperty("proteins_100g") BigDecimal proteins100g,
            @JsonProperty("carbohydrates_100g") BigDecimal carbohydrates100g,
            @JsonProperty("fat_100g") BigDecimal fat100g,
            @JsonProperty("fiber_100g") BigDecimal fiber100g,
            @JsonProperty("sugars_100g") BigDecimal sugars100g,
            @JsonProperty("sodium_100g") BigDecimal sodium100g
    ) {
    }
}
