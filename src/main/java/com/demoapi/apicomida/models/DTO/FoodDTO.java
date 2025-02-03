package com.demoapi.apicomida.models.DTO;

import com.demoapi.apicomida.util.Unit;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Data
@Getter
@Setter
@Builder
public class FoodDTO {
    @JsonProperty
    @NotBlank
    private String category;

    @JsonProperty
    @NotBlank
    private String name;

    @JsonProperty
    @NotNull
    @Positive
    private Double quantity;

    @JsonProperty
    @NotNull
    private Unit unit;

    @JsonProperty
    @NotNull
    @PositiveOrZero
    private Double calories;

    @JsonProperty
    @PositiveOrZero
    private Double protein;

    @JsonProperty
    @PositiveOrZero
    private Double carb;

    @JsonProperty
    @PositiveOrZero
    private Double fat;

    @JsonProperty
    @PositiveOrZero
    private Double sugar;

    @JsonProperty
    @PositiveOrZero
    private Double sodium;
}
