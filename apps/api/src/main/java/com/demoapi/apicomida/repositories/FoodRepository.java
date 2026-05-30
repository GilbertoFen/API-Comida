package com.demoapi.apicomida.repositories;

import com.demoapi.apicomida.models.FoodModel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodRepository extends JpaRepository<FoodModel, Long> {
    List<FoodModel> findByNameContainingIgnoreCase(String name);
}
