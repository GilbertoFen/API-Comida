package com.demoapi.apicomida.services;

import com.demoapi.apicomida.models.DTO.MealDTO;
import com.demoapi.apicomida.models.MealModel;
import com.demoapi.apicomida.models.RecipeModel;
import com.demoapi.apicomida.models.UserModel;
import com.demoapi.apicomida.repositories.MealRepository;
import com.demoapi.apicomida.repositories.RecipeRepository;
import com.demoapi.apicomida.repositories.UserRepository;
import com.demoapi.apicomida.util.Mappers.MealMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Service
public class MealService {
    private final MealRepository mealRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;

    @Autowired
    public MealService(MealRepository mealRepository, UserRepository userRepository, RecipeRepository recipeRepository) {
        this.mealRepository = mealRepository;
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;
    }

    private static ResponseEntity<String> get() {
        return ResponseEntity.status(NOT_FOUND).body("Meal not found");
    }

    public ResponseEntity<?> createMeal(MealDTO mealDTO) {
        try {
            Optional<UserModel> userOptional = userRepository.findById(mealDTO.getIdUser());

            if (userOptional.isEmpty()) {
                return ResponseEntity.status(BAD_REQUEST).body("User not found");
            }

            UserModel user = userOptional.get();
            List<RecipeModel> recipes = recipeRepository.findAllById(mealDTO.getRecipeIds());
            double totalCalories = recipes.stream().mapToDouble(RecipeModel::getCalories).sum();

            MealModel mealModel = MealModel.builder()
                    .userId(user)
                    .recipes(recipes)
                    .date(mealDTO.getDate())
                    .totalCalories(totalCalories)
                    .build();

            mealRepository.save(mealModel);

            return ResponseEntity.status(CREATED).body(MealMapper.toDTO(mealModel));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Error creating meal");
        }
    }

    public ResponseEntity<MealDTO> getMealById(long id) {
        return mealRepository.findById(id)
                .map(meal -> ResponseEntity.ok(MealMapper.toDTO(meal)))
                .orElse(ResponseEntity.status(NOT_FOUND).build());
    }


    public ResponseEntity<?> deleteMeal(long id) {
        if (mealRepository.existsById(id)) {
            mealRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(NOT_FOUND).body("Meal not found");
        }
    }
    /*
    public ResponseEntity<MealDTO> updateMeal(long id, MealDTO mealDTO) {
        ResponseEntity<MealDTO> mealNotFound = mealRepository.findById(id)
                .map(existingMeal -> {
                    List<RecipeModel> updatedRecipes = recipeRepository.findAllById(mealDTO.getRecipeIds());
                    double totalCalories = updatedRecipes.stream().mapToDouble(RecipeModel::getCalories).sum();

                    existingMeal.setRecipes(updatedRecipes);
                    existingMeal.setDate(mealDTO.getDate());
                    existingMeal.setTotalCalories(totalCalories);

                    mealRepository.save(existingMeal);

                    return ResponseEntity.ok(MealMapper.toDTO(existingMeal));
                })
                .orElseGet(() -> get());
        return mealNotFound;
    }*/
    /*
    public ResponseEntity<List<MealDTO>> getMealsByUser(Long userId) {
        List<MealModel> meals = mealRepository.findByUserId(userRepository.findById(userId));
        List<MealDTO> mealDTOs = meals.stream().map(MealMapper::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(mealDTOs);
    }*/
}
