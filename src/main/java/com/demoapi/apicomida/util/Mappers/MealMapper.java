package com.demoapi.apicomida.util.Mappers;

import com.demoapi.apicomida.models.DTO.MealDTO;
import com.demoapi.apicomida.models.DTO.RecipeDTO.RecipeDTO;
import com.demoapi.apicomida.models.MealModel;

import java.util.stream.Collectors;

public class MealMapper {

    public static MealDTO toDTO(MealModel mealModel) {
        return MealDTO.builder()
                .idUser(mealModel.getUserId().getIdUser())
                .date(mealModel.getDate())
                .recipeIds(mealModel.getRecipes().stream().map(recipe -> recipe.getId()).collect(Collectors.toList())) // Se devuelven solo los IDs de las recetas
                .build();
    }
}
