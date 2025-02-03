package com.demoapi.apicomida.util.Mappers;

import com.demoapi.apicomida.models.DTO.RecipeDTO;
import com.demoapi.apicomida.models.RecipeModel;

import java.util.stream.Collectors;

public class RecipeMapper {
    public static RecipeModel toModel(RecipeDTO recipeDTO) {
        return RecipeModel.builder()
                .name(recipeDTO.getName())
                .description(recipeDTO.getDescription())
                .instructions(recipeDTO.getInstructions())
                /*.ingredients(recipeDTO.getIngredients().stream()
                        .map(FoodMapper::toModel)
                        .collect(Collectors.toList()))*/
                .build();
    }

    public static RecipeDTO toDTO(RecipeModel recipeModel) {
        return RecipeDTO.builder()
                .name(recipeModel.getName())
                .description(recipeModel.getDescription())
                .instructions(recipeModel.getInstructions())
                /*.ingredients(recipeModel.getIngredients().stream()
                        .map(FoodMapper::toDTO)
                        .collect(Collectors.toList()))*/
                .build();
    }
}
