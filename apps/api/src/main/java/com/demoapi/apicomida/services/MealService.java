package com.demoapi.apicomida.services;

import com.demoapi.apicomida.models.DTO.MealDTO;
import com.demoapi.apicomida.models.MealModel;
import com.demoapi.apicomida.models.RecipeModel;
import com.demoapi.apicomida.models.UserModel;
import com.demoapi.apicomida.repositories.MealRepository;
import com.demoapi.apicomida.repositories.RecipeRepository;
import com.demoapi.apicomida.repositories.UserRepository;
import com.demoapi.apicomida.util.Mappers.MealMapper;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MealService {
    private final MealRepository mealRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;

    public MealService(MealRepository mealRepository, UserRepository userRepository, RecipeRepository recipeRepository) {
        this.mealRepository = mealRepository;
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;
    }

    public MealDTO createMeal(MealDTO mealDTO) {
        UserModel user = userRepository.findById(mealDTO.getIdUser())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found"));

        List<RecipeModel> recipes = recipeRepository.findAllById(mealDTO.getRecipeIds());
        if (recipes.size() != mealDTO.getRecipeIds().size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "One or more recipes were not found");
        }

        double totalCalories = recipes.stream()
                .mapToDouble(recipe -> recipe.getCalories() == null ? 0.0 : recipe.getCalories())
                .sum();

        MealModel mealModel = new MealModel();
        mealModel.setUserId(user);
        mealModel.setRecipes(recipes);
        mealModel.setDate(mealDTO.getDate());
        mealModel.setTotalCalories(totalCalories);

        return MealMapper.toDTO(mealRepository.save(mealModel));
    }

    public MealDTO getMealById(long id) {
        MealModel mealModel = mealRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Meal not found"));
        return MealMapper.toDTO(mealModel);
    }

    public void deleteMeal(long id) {
        if (!mealRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Meal not found");
        }
        mealRepository.deleteById(id);
    }
}
