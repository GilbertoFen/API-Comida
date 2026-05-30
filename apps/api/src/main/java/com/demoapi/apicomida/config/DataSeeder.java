package com.demoapi.apicomida.config;

import com.demoapi.apicomida.models.FoodModel;
import com.demoapi.apicomida.models.RecipeModel;
import com.demoapi.apicomida.models.UserModel;
import com.demoapi.apicomida.repositories.FoodRepository;
import com.demoapi.apicomida.repositories.RecipeRepository;
import com.demoapi.apicomida.repositories.UserRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedDemoData(
            UserRepository userRepository,
            FoodRepository foodRepository,
            RecipeRepository recipeRepository
    ) {
        return args -> {
            if (userRepository.count() > 0 || foodRepository.count() > 0) {
                return;
            }

            UserModel demoUser = new UserModel();
            demoUser.setName("Demo User");
            demoUser.setAge(28);
            demoUser.setEmail("demo@appfoodspring.local");
            demoUser.setPassword("demo123");
            demoUser.setWeight(72.5f);
            demoUser.setHeight(1.75f);
            demoUser.setExerciseLevel(3);
            demoUser = userRepository.save(demoUser);

            FoodModel chicken = new FoodModel();
            chicken.setCountry("Mexico");
            chicken.setCategory("Protein");
            chicken.setName("Chicken Breast");
            chicken.setQuantity(100.0);
            chicken.setUnit(1);
            chicken.setCalories(165.0);
            chicken.setProtein(31.0);
            chicken.setCarb(0.0);
            chicken.setFat(3.6);
            chicken.setSugar(0.0);
            chicken.setSodium(74.0);
            chicken = foodRepository.save(chicken);

            FoodModel rice = new FoodModel();
            rice.setCountry("Mexico");
            rice.setCategory("Carbohydrate");
            rice.setName("Cooked Rice");
            rice.setQuantity(100.0);
            rice.setUnit(1);
            rice.setCalories(130.0);
            rice.setProtein(2.7);
            rice.setCarb(28.0);
            rice.setFat(0.3);
            rice.setSugar(0.1);
            rice.setSodium(1.0);
            rice = foodRepository.save(rice);

            FoodModel avocado = new FoodModel();
            avocado.setCountry("Mexico");
            avocado.setCategory("Fat");
            avocado.setName("Avocado");
            avocado.setQuantity(100.0);
            avocado.setUnit(1);
            avocado.setCalories(160.0);
            avocado.setProtein(2.0);
            avocado.setCarb(9.0);
            avocado.setFat(15.0);
            avocado.setSugar(0.7);
            avocado.setSodium(7.0);
            avocado = foodRepository.save(avocado);

            RecipeModel chickenBowl = new RecipeModel();
            chickenBowl.setName("Chicken Bowl");
            chickenBowl.setDescription("Balanced bowl with chicken, rice and avocado.");
            chickenBowl.setInstructions("Cook the rice, grill the chicken, slice the avocado and serve together.");
            chickenBowl.setIdUser(demoUser);
            chickenBowl.setIngredients(List.of(chicken, rice, avocado));
            chickenBowl.setCalories(455.0);
            chickenBowl.setProtein(35.7);
            chickenBowl.setCarb(37.0);
            chickenBowl.setFat(18.9);
            chickenBowl.setSugar(0.8);
            chickenBowl.setSodium(82.0);
            recipeRepository.save(chickenBowl);
        };
    }
}
