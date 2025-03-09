package com.demoapi.apicomida.util.Mappers;

import com.demoapi.apicomida.models.DTO.RecipeDTO.*;
import com.demoapi.apicomida.models.FoodModel;
import com.demoapi.apicomida.models.RecipeModel;

import java.util.List;
import java.util.stream.Collectors;

public class RecipeMapper {
    public static RecipeModel toModel(CreateRecipeDTO recipeCreateDTO, List<FoodModel> ingredients) {
        return RecipeModel.builder()
                .name(recipeCreateDTO.getName())
                .description(recipeCreateDTO.getDescription())
                .instructions(recipeCreateDTO.getInstructions())
                .ingredients(ingredients) 
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
                .fat(recipeModel.getFat())
                .carb(recipeModel.getCarb())
                .sugar(recipeModel.getSugar())
                .protein(recipeModel.getProtein())
                .sodium(recipeModel.getSodium())
                .calories(recipeModel.getCalories())
                .build();
    }
}

