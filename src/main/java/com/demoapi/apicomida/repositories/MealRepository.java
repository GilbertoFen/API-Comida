package com.demoapi.apicomida.repositories;

import com.demoapi.apicomida.models.MealModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MealRepository extends JpaRepository<MealModel,Long> {

}
