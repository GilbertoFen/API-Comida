package com.demoapi.apicomida.models.DTO.RecipeDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.Optional;

@Data
@Getter
@Setter
@Builder
public class CreateRecipeDTO {
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
    @NotEmpty(message = "ingredientIds no debe estar vacío")
    private List<Long> ingredientIds;

    @JsonProperty
    @NotNull
    private long idUser;

}
