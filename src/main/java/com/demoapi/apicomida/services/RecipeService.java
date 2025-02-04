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
import org.springframework.web.server.ResponseStatusException;

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

    public ResponseEntity<?> createRecipe(CreateRecipeDTO createRecipeDTO) {
        try {
            Optional<UserModel> optionalUser = Optional.ofNullable(userRepository.findById(createRecipeDTO.getIdUser()));
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User not found with id: " + createRecipeDTO.getIdUser());
            }
            UserModel user = optionalUser.get();

            RecipeModel recipeModel = RecipeMapper.toModel(createRecipeDTO);

            List<FoodModel> ingredients = createRecipeDTO.getIngredients().stream()
                    .map(ingredientDTO -> foodRepository.findByName(ingredientDTO.getName())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                    "Ingredient not found: " + ingredientDTO.getName())))
                    .collect(Collectors.toList());
            recipeModel.setIngredients(ingredients);
            recipeModel.setIdUser(user);
            calculateRecipeNutrition(recipeModel);
            recipeRepository.save(recipeModel);

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating recipe: " + e.getMessage());
        }
    }


    public void calculateRecipeNutrition(RecipeModel recipe) {
        double totalCalories = 0.0;
        double totalProtein = 0.0;
        double totalCarb = 0.0;
        double totalFat = 0.0;
        double totalSugar = 0.0;
        double totalSodium = 0.0;

        for (FoodModel ingredient : recipe.getIngredients()) {
            totalCalories += ingredient.getCalories();
            totalProtein += ingredient.getProtein() ;
            totalCarb += ingredient.getCarb();
            totalFat += ingredient.getFat() ;
            totalSugar += ingredient.getSugar() ;
            totalSodium += ingredient.getSodium() ;
        }

        recipe.setCalories(totalCalories);
        recipe.setProtein(totalProtein);
        recipe.setCarb(totalCarb);
        recipe.setFat(totalFat);
        recipe.setSugar(totalSugar);
        recipe.setSodium(totalSodium);
    }

    public List<RecipeDTO> getAllRecipes() {
        List<RecipeModel> recipes = recipeRepository.findAll();
        return recipes.stream()
                .map(RecipeMapper::toDTO)
                .collect(Collectors.toList());
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
