package com.demoapi.apicomida.repositories;

import com.demoapi.apicomida.models.Recipe;
import com.demoapi.apicomida.models.RecipeIngredient;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, UUID> {
    List<RecipeIngredient> findByRecipe(Recipe recipe);
    Optional<RecipeIngredient> findByIdAndRecipe(UUID id, Recipe recipe);
    void deleteByRecipe(Recipe recipe);
}
