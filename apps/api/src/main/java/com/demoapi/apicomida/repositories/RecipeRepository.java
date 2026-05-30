package com.demoapi.apicomida.repositories;

import com.demoapi.apicomida.models.RecipeModel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends JpaRepository<RecipeModel, Long> {
    List<RecipeModel> findByNameContainingIgnoreCase(String name);
}
