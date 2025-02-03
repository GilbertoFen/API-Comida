package com.demoapi.apicomida.services;

import com.demoapi.apicomida.models.DTO.MealDTO;
import com.demoapi.apicomida.models.MealModel;
import com.demoapi.apicomida.repositories.MealRepository;
import com.demoapi.apicomida.util.Mappers.MealMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class MealService {
    private final MealRepository mealRepository;

    @Autowired
    public MealService(MealRepository mealRepository) {
        this.mealRepository = mealRepository;
    }

    public ResponseEntity<?> createMeal(MealDTO mealDTO) {
        try {
            MealModel mealModel = MealMapper.toModel(mealDTO);
            mealRepository.save(mealModel);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating meal");
        }
    }

    public ResponseEntity<MealDTO> getMealById(long id) {
        return mealRepository.findById(id)
                .map(meal -> ResponseEntity.ok(MealMapper.toDTO(meal)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build()); // Devuelve un ResponseEntity<MealDTO> vacío
    }

    public ResponseEntity<?> updateMeal(long id, MealDTO mealDTO) {
        return mealRepository.findById(id)
                .map(existingMeal -> {
                    MealModel updatedMeal = MealMapper.toModel(mealDTO);
                    updatedMeal.setId(id);
                    mealRepository.save(updatedMeal);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Meal not found"));
    }

    public ResponseEntity<?> deleteMeal(long id) {
        if (mealRepository.existsById(id)) {
            mealRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Meal not found");
        }
    }

}
