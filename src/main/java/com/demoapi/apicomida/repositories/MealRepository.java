package com.demoapi.apicomida.repositories;

import com.demoapi.apicomida.models.MealModel;
import com.demoapi.apicomida.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MealRepository extends JpaRepository<MealModel,Long> {
    List<MealModel> findByUserId(UserModel idUser);

}
