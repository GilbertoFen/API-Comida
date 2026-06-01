package com.demoapi.apicomida.repositories;

import com.demoapi.apicomida.models.RecipePost;
import com.demoapi.apicomida.models.RecipePostLike;
import com.demoapi.apicomida.models.UserAccount;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipePostLikeRepository extends JpaRepository<RecipePostLike, UUID> {
    long countByRecipePost(RecipePost recipePost);
    Optional<RecipePostLike> findByRecipePostAndUser(RecipePost recipePost, UserAccount user);
}
