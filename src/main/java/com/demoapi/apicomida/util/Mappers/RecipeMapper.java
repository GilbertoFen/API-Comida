package com.demoapi.apicomida.util.Mappers;

import com.demoapi.apicomida.models.DTO.RecipeDTO.*;
import com.demoapi.apicomida.models.RecipeModel;
import java.util.stream.Collectors;

public class RecipeMapper {
    public static RecipeModel toModel(CreateRecipeDTO recipeCreateDTO) {
        return RecipeModel.builder()
                .name(recipeCreateDTO.getName())
                .description(recipeCreateDTO.getDescription())
                .ingredients(
                        recipeCreateDTO.getIngredients().stream()
                                .map(FoodMapper::toModel)
                                .collect(Collectors.toList())
                )
                .build();
    }
    public static RecipeDTO toDTO(RecipeModel recipeModel) {
        return RecipeDTO.builder()
                .name(recipeModel.getName())
                .description(recipeModel.getDescription())
                .ingredients(
                        recipeModel.getIngredients().stream()
                                .map(FoodMapper::toDTO)
                                .collect(Collectors.toList())
                )
                .calories(recipeModel.getCalories())
                .build();
    }
}

