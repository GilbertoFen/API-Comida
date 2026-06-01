package com.demoapi.apicomida.repositories;

import com.demoapi.apicomida.models.Recipe;
import com.demoapi.apicomida.models.UserAccount;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<Recipe, UUID> {
    List<Recipe> findByUserOrderByCreatedAtDesc(UserAccount user);
    List<Recipe> findByIsPublicTrueOrderByCreatedAtDesc();
    List<Recipe> findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(String query);
}
