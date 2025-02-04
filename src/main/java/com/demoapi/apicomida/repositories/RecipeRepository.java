package com.demoapi.apicomida.repositories;

import com.demoapi.apicomida.models.RecipeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends JpaRepository<RecipeModel, Long> {
     RecipeModel findByName(String name);

}
