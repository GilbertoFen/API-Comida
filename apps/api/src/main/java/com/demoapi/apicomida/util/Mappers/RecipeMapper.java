package com.demoapi.apicomida.util.Mappers;

import com.demoapi.apicomida.models.DTO.RecipeDTO.CreateRecipeDTO;
import com.demoapi.apicomida.models.DTO.RecipeDTO.RecipeDTO;
import com.demoapi.apicomida.models.FoodModel;
import com.demoapi.apicomida.models.RecipeModel;
import java.util.List;

public class RecipeMapper {

    private RecipeMapper() {
    }

    public static RecipeModel toModel(CreateRecipeDTO recipeCreateDTO, List<FoodModel> ingredients) {
        RecipeModel recipeModel = new RecipeModel();
        recipeModel.setName(recipeCreateDTO.getName());
        recipeModel.setDescription(recipeCreateDTO.getDescription());
        recipeModel.setInstructions(recipeCreateDTO.getInstructions());
        recipeModel.setIngredients(ingredients);
        return recipeModel;
    }

    public static RecipeDTO toDTO(RecipeModel recipeModel) {
        RecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setId(recipeModel.getId());
        recipeDTO.setName(recipeModel.getName());
        recipeDTO.setDescription(recipeModel.getDescription());
        recipeDTO.setInstructions(recipeModel.getInstructions());
        recipeDTO.setIngredients(recipeModel.getIngredients().stream().map(FoodMapper::toDTO).toList());
        recipeDTO.setFat(recipeModel.getFat());
        recipeDTO.setCarb(recipeModel.getCarb());
        recipeDTO.setSugar(recipeModel.getSugar());
        recipeDTO.setProtein(recipeModel.getProtein());
        recipeDTO.setSodium(recipeModel.getSodium());
        recipeDTO.setCalories(recipeModel.getCalories());
        recipeDTO.setUserId(recipeModel.getIdUser().getIdUser());
        return recipeDTO;
    }
}
