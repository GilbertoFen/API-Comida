package com.demoapi.apicomida.services;

import com.demoapi.apicomida.models.DTO.FoodDTO;
import com.demoapi.apicomida.models.FoodModel;
import com.demoapi.apicomida.repositories.FoodRepository;
import com.demoapi.apicomida.util.Mappers.FoodMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FoodService {
    private final FoodRepository foodRepository;

    @Autowired
    public FoodService(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }

    public ResponseEntity<?> createFood(@RequestBody FoodDTO foodDTO) {
        try {
            FoodModel foodModel = FoodMapper.toModel(foodDTO);
            foodRepository.save(foodModel);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating food");
        }
    }

    public ResponseEntity<List<FoodDTO>> getAllFoods() {
        List<FoodDTO> foods = foodRepository.findAll()
                .stream()
                .map(FoodMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(foods);
    }

    public ResponseEntity<FoodDTO> getFoodById(Long id) {
        Optional<FoodModel> foodModel = foodRepository.findById(id);
        if (foodModel.isPresent()) {
            return ResponseEntity.ok(FoodMapper.toDTO(foodModel.get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    public ResponseEntity<?> updateFood(Long id, FoodDTO foodDTO) {
        Optional<FoodModel> existingFood = foodRepository.findById(id);
        if (existingFood.isPresent()) {
            FoodModel updatedFood = FoodMapper.toModel(foodDTO);
            updatedFood.setId(id);
            foodRepository.save(updatedFood);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Food not found");
    }

    public ResponseEntity<?> deleteFood(Long id) {
        if (foodRepository.existsById(id)) {
            foodRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Food not found");
    }
}
