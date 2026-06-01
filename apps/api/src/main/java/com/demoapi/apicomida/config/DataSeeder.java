package com.demoapi.apicomida.config;

import com.demoapi.apicomida.models.Exercise;
import com.demoapi.apicomida.models.Food;
import com.demoapi.apicomida.repositories.ExerciseRepository;
import com.demoapi.apicomida.repositories.FoodRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedCatalogs(FoodRepository foodRepository, ExerciseRepository exerciseRepository) {
        return args -> {
            if (foodRepository.count() == 0) {
                foodRepository.saveAll(List.of(
                        createFood("Chicken Breast", "Protein", "100", "g", "165", "31", "0", "3.6", "0", "0", "74", "seed"),
                        createFood("Cooked Rice", "Carbohydrate", "100", "g", "130", "2.7", "28", "0.3", "0.4", "0.1", "1", "seed"),
                        createFood("Avocado", "Fat", "100", "g", "160", "2", "9", "15", "7", "0.7", "7", "seed")
                ));
            }
            if (exerciseRepository.count() == 0) {
                exerciseRepository.saveAll(List.of(
                        createExercise("Push Up", "chest", "bodyweight", "beginner", 3, "12-15", null),
                        createExercise("Squat", "legs", "bodyweight", "beginner", 4, "10-12", null),
                        createExercise("Plank", "core", "bodyweight", "beginner", 3, "time", 45)
                ));
            }
        };
    }

    private Food createFood(
            String name,
            String category,
            String servingSize,
            String servingUnit,
            String calories,
            String protein,
            String carbs,
            String fat,
            String fiber,
            String sugar,
            String sodium,
            String source
    ) {
        Food food = new Food();
        food.setName(name);
        food.setCategory(category);
        food.setServingSize(new BigDecimal(servingSize));
        food.setServingUnit(servingUnit);
        food.setCaloriesPer100g(new BigDecimal(calories));
        food.setProteinPer100g(new BigDecimal(protein));
        food.setCarbsPer100g(new BigDecimal(carbs));
        food.setFatPer100g(new BigDecimal(fat));
        food.setFiberPer100g(new BigDecimal(fiber));
        food.setSugarPer100g(new BigDecimal(sugar));
        food.setSodiumPer100g(new BigDecimal(sodium));
        food.setExternalSource(source.toUpperCase());
        food.setVerified(true);
        return food;
    }

    private Exercise createExercise(
            String name,
            String muscleGroup,
            String equipment,
            String difficulty,
            Integer recommendedSets,
            String recommendedReps,
            Integer recommendedTimeSeconds
    ) {
        Exercise exercise = new Exercise();
        exercise.setName(name);
        exercise.setMuscleGroup(muscleGroup);
        exercise.setEquipment(equipment);
        exercise.setDifficulty(difficulty);
        exercise.setRecommendedSets(recommendedSets);
        exercise.setRecommendedReps(recommendedReps);
        exercise.setRecommendedTimeSeconds(recommendedTimeSeconds);
        return exercise;
    }
}
