package com.demoapi.apicomida.controllers;

import com.demoapi.apicomida.models.DTO.FoodDTO;
import com.demoapi.apicomida.services.FoodService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/food")
public class FoodController {
    @Autowired
    private final FoodService foodService;

    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @PostMapping
    public ResponseEntity<String> createFood(@Valid @RequestBody FoodDTO foodDTO) {
        foodService.createFood(foodDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("Comida creada con éxito.");
    }

    @GetMapping
    public ResponseEntity<List<FoodDTO>> getAllFoods() {
        List<FoodDTO> foods = foodService.getAllFoods().getBody();
        return ResponseEntity.ok(foods);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodDTO> getFoodById(@PathVariable Long id) {
        FoodDTO food = foodService.getFoodById(id).getBody();
        return ResponseEntity.ok(food);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateFood(@PathVariable Long id, @Valid @RequestBody FoodDTO foodDTO) {
        foodService.updateFood(id, foodDTO);
        return ResponseEntity.ok("Comida actualizada con éxito.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFood(@PathVariable Long id) {
        boolean deleted = foodService.deleteFood(id).hasBody();
        if (deleted) {
            return ResponseEntity.ok("Comida eliminada con éxito.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comida no encontrada.");
        }
    }
}
