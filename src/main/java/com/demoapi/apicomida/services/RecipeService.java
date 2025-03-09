package com.demoapi.apicomida.services;

import com.demoapi.apicomida.models.DTO.RecipeDTO.*;
import com.demoapi.apicomida.models.FoodModel;
import com.demoapi.apicomida.models.RecipeModel;
import com.demoapi.apicomida.models.UserModel;
import com.demoapi.apicomida.repositories.FoodRepository;
import com.demoapi.apicomida.repositories.RecipeRepository;
import com.demoapi.apicomida.repositories.UserRepository;
import com.demoapi.apicomida.util.Mappers.RecipeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final FoodRepository foodRepository;
    private final UserRepository userRepository;

    @Autowired
    public RecipeService(RecipeRepository recipeRepository, FoodRepository foodRepository, UserRepository userRepository) {
        this.recipeRepository = recipeRepository;
        this.foodRepository = foodRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> createRecipe(CreateRecipeDTO recipeCreateDTO) {

        Optional<UserModel> userOptional = userRepository.findById(recipeCreateDTO.getIdUser());
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
        UserModel user = userOptional.get();

        List<FoodModel> ingredients = foodRepository.findAllById(recipeCreateDTO.getIngredientIds());
        if (ingredients.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontraron los ingredientes");
        }

        RecipeModel recipeModel = RecipeMapper.toModel(recipeCreateDTO, ingredients);
        recipeModel.setIdUser(user);

        calculateRecipeNutrition(recipeModel);

        recipeRepository.save(recipeModel);

        return ResponseEntity.status(HttpStatus.CREATED).body(RecipeMapper.toDTO(recipeModel));
    }



    public ResponseEntity<List<RecipeDTO>> getAllRecipes() {
        List<RecipeDTO> recipes = recipeRepository.findAll().stream()
                .map(RecipeMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(recipes);
    }

    public void calculateRecipeNutrition(RecipeModel recipe) {
        double totalCalories = 0.0;
        double totalProtein = 0.0;
        double totalCarb = 0.0;
        double totalFat = 0.0;
        double totalSugar = 0.0;
        double totalSodium = 0.0;

        for (FoodModel ingredient : recipe.getIngredients()) {
            double ingredientQuantity = ingredient.getQuantity();
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


    public ResponseEntity<RecipeDTO> getRecipeById(Long id) {
        Optional<RecipeModel> recipeModel = recipeRepository.findById(id);
        return recipeModel.map(model -> ResponseEntity
                        .ok(RecipeMapper.
                                toDTO(model))).
                orElseGet(() -> ResponseEntity.
                        status(HttpStatus.NOT_FOUND).
                        build());
    }

    public ResponseEntity<?> updateRecipe(Long id, RecipeDTO recipeDTO) {
        Optional<RecipeModel> existingRecipeOpt = recipeRepository.findById(id);
        if (existingRecipeOpt.isPresent()) {
            RecipeModel existingRecipe = existingRecipeOpt.get();
            if (recipeDTO.getName() != null) {
                existingRecipe.setName(recipeDTO.getName());
            }
            if (recipeDTO.getDescription() != null) {
                existingRecipe.setDescription(recipeDTO.getDescription());
            }
            if (recipeDTO.getIngredients() != null) {
                List<FoodModel> ingredients = recipeDTO.getIngredients().stream()
                        .map(ingredientDTO -> foodRepository.findByName(ingredientDTO.getName())
                                .orElseThrow(() -> new RuntimeException("Ingredient not found: " + ingredientDTO.getName())))
                        .collect(Collectors.toList());
                existingRecipe.setIngredients(ingredients);
            }
            calculateRecipeNutrition(existingRecipe);

            recipeRepository.save(existingRecipe);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recipe not found");
    }


    public ResponseEntity<?> deleteRecipe(Long id) {
        if (recipeRepository.existsById(id)) {
            recipeRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recipe not found");
    }

}
