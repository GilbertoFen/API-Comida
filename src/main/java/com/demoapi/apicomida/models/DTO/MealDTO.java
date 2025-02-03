package com.demoapi.apicomida.models.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
@Builder
public class MealDTO {
    @JsonProperty
    @NotNull
    private Long idUser;

    @JsonProperty
    @NotEmpty
    private List<RecipeDTO> recipes;

    @JsonProperty
    @NotBlank
    private String date;

    @JsonProperty
    @PositiveOrZero
    private Double totalCalories;
}
