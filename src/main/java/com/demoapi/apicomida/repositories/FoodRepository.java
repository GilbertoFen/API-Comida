package com.demoapi.apicomida.repositories;

import com.demoapi.apicomida.models.FoodModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FoodRepository extends JpaRepository<FoodModel, Long> {
    Optional<FoodModel> findByName(String name);
    Optional<FoodModel> findByCategory(String category);
}
