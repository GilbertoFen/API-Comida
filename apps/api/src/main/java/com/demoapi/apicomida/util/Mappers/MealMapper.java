package com.demoapi.apicomida.util.Mappers;

import com.demoapi.apicomida.models.DTO.MealDTO;
import com.demoapi.apicomida.models.MealModel;

public class MealMapper {

    private MealMapper() {
    }

    public static MealDTO toDTO(MealModel mealModel) {
        MealDTO mealDTO = new MealDTO();
        mealDTO.setId(mealModel.getId());
        mealDTO.setIdUser(mealModel.getUserId().getIdUser());
        mealDTO.setDate(mealModel.getDate());
        mealDTO.setRecipeIds(mealModel.getRecipes().stream().map(recipe -> recipe.getId()).toList());
        mealDTO.setTotalCalories(mealModel.getTotalCalories());
        return mealDTO;
    }
}
