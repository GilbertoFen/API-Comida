package com.demoapi.apicomida.services;

import com.demoapi.apicomida.models.DTO.FoodDTO;
import com.demoapi.apicomida.models.FoodModel;
import com.demoapi.apicomida.repositories.FoodRepository;
import com.demoapi.apicomida.util.Mappers.FoodMapper;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FoodService {
    private final FoodRepository foodRepository;

    public FoodService(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }

    public FoodDTO createFood(FoodDTO foodDTO) {
        FoodModel savedFood = foodRepository.save(FoodMapper.toModel(foodDTO));
        return FoodMapper.toDTO(savedFood);
    }

    public List<FoodDTO> getFoods(String name) {
        List<FoodModel> foods = (name == null || name.isBlank())
                ? foodRepository.findAll()
                : foodRepository.findByNameContainingIgnoreCase(name.trim());

        return foods.stream().map(FoodMapper::toDTO).toList();
    }

    public FoodDTO getFoodById(Long id) {
        FoodModel foodModel = foodRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Food not found"));
        return FoodMapper.toDTO(foodModel);
    }

    public FoodDTO updateFood(Long id, FoodDTO foodDTO) {
        FoodModel existingFood = foodRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Food not found"));

        existingFood.setCountry(foodDTO.getCountry());
        existingFood.setCategory(foodDTO.getCategory());
        existingFood.setName(foodDTO.getName());
        existingFood.setQuantity(foodDTO.getQuantity());
        existingFood.setUnit(foodDTO.getUnit());
        existingFood.setCalories(foodDTO.getCalories());
        existingFood.setProtein(foodDTO.getProtein());
        existingFood.setCarb(foodDTO.getCarb());
        existingFood.setFat(foodDTO.getFat());
        existingFood.setSugar(foodDTO.getSugar());
        existingFood.setSodium(foodDTO.getSodium());

        return FoodMapper.toDTO(foodRepository.save(existingFood));
    }

    public void deleteFood(Long id) {
        if (!foodRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Food not found");
        }
        foodRepository.deleteById(id);
    }
}
