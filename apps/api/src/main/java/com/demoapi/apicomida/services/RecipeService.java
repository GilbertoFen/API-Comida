package com.demoapi.apicomida.services;

import com.demoapi.apicomida.models.DTO.RecipeDTO.CreateRecipeDTO;
import com.demoapi.apicomida.models.DTO.RecipeDTO.RecipeDTO;
import com.demoapi.apicomida.models.FoodModel;
import com.demoapi.apicomida.models.RecipeModel;
import com.demoapi.apicomida.models.UserModel;
import com.demoapi.apicomida.repositories.FoodRepository;
import com.demoapi.apicomida.repositories.RecipeRepository;
import com.demoapi.apicomida.repositories.UserRepository;
import com.demoapi.apicomida.util.Mappers.RecipeMapper;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final FoodRepository foodRepository;
    private final UserRepository userRepository;

    public RecipeService(
            RecipeRepository recipeRepository,
            FoodRepository foodRepository,
            UserRepository userRepository
    ) {
        this.recipeRepository = recipeRepository;
        this.foodRepository = foodRepository;
        this.userRepository = userRepository;
    }

    public RecipeDTO createRecipe(CreateRecipeDTO recipeCreateDTO) {
        UserModel user = userRepository.findById(recipeCreateDTO.getIdUser())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<FoodModel> ingredients = foodRepository.findAllById(recipeCreateDTO.getIngredientIds());
        if (ingredients.size() != recipeCreateDTO.getIngredientIds().size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "One or more ingredients were not found");
        }

        RecipeModel recipeModel = RecipeMapper.toModel(recipeCreateDTO, ingredients);
        recipeModel.setIdUser(user);
        calculateRecipeNutrition(recipeModel);

        return RecipeMapper.toDTO(recipeRepository.save(recipeModel));
    }

    public List<RecipeDTO> getRecipes(String name) {
        List<RecipeModel> recipes = (name == null || name.isBlank())
                ? recipeRepository.findAll()
                : recipeRepository.findByNameContainingIgnoreCase(name.trim());
        return recipes.stream().map(RecipeMapper::toDTO).toList();
    }

    public RecipeDTO getRecipeById(Long id) {
        RecipeModel recipeModel = recipeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found"));
        return RecipeMapper.toDTO(recipeModel);
    }

    public RecipeDTO updateRecipe(Long id, CreateRecipeDTO recipeDTO) {
        RecipeModel existingRecipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found"));

        UserModel user = userRepository.findById(recipeDTO.getIdUser())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<FoodModel> ingredients = foodRepository.findAllById(recipeDTO.getIngredientIds());
        if (ingredients.size() != recipeDTO.getIngredientIds().size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "One or more ingredients were not found");
        }

        existingRecipe.setName(recipeDTO.getName());
        existingRecipe.setDescription(recipeDTO.getDescription());
        existingRecipe.setInstructions(recipeDTO.getInstructions());
        existingRecipe.setIngredients(ingredients);
        existingRecipe.setIdUser(user);

        calculateRecipeNutrition(existingRecipe);

        return RecipeMapper.toDTO(recipeRepository.save(existingRecipe));
    }

    public void deleteRecipe(Long id) {
        if (!recipeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found");
        }
        recipeRepository.deleteById(id);
    }

    private void calculateRecipeNutrition(RecipeModel recipe) {
        double totalCalories = 0.0;
        double totalProtein = 0.0;
        double totalCarb = 0.0;
        double totalFat = 0.0;
        double totalSugar = 0.0;
        double totalSodium = 0.0;

        for (FoodModel ingredient : recipe.getIngredients()) {
            double ingredientQuantity = ingredient.getQuantity() == null ? 0.0 : ingredient.getQuantity();
            double quantity = ingredientQuantity / 100;
            totalCalories += ingredient.getCalories() * quantity;
            totalProtein += ingredient.getProtein() * quantity;
            totalCarb += ingredient.getCarb() * quantity;
            totalFat += ingredient.getFat() * quantity;
            totalSugar += ingredient.getSugar() * quantity;
            totalSodium += ingredient.getSodium() * quantity;
        }

        recipe.setCalories(totalCalories);
        recipe.setProtein(totalProtein);
        recipe.setCarb(totalCarb);
        recipe.setFat(totalFat);
        recipe.setSugar(totalSugar);
        recipe.setSodium(totalSodium);
    }
}
