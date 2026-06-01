package com.demoapi.apicomida.repositories;

import com.demoapi.apicomida.models.RecipePost;
import com.demoapi.apicomida.models.UserAccount;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipePostRepository extends JpaRepository<RecipePost, UUID> {
    List<RecipePost> findAllByOrderByCreatedAtDesc();
    List<RecipePost> findByUserOrderByCreatedAtDesc(UserAccount user);
}
