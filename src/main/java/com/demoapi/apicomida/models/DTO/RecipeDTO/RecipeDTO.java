package com.demoapi.apicomida.models.DTO.RecipeDTO;

import com.demoapi.apicomida.models.DTO.FoodDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import java.util.List;

@Data
@Getter
@Setter
@Builder
public class RecipeDTO {
    @JsonProperty
    @NotBlank
    private String name;

    @JsonProperty
    @NotBlank
    private String description;

    @JsonProperty
    @NotBlank
    private String instructions;

    @JsonProperty
    @NotEmpty
    private List<FoodDTO> ingredients;

    @JsonProperty
    private Double calories;
}
