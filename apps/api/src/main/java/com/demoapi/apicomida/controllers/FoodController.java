package com.demoapi.apicomida.controllers;

import com.demoapi.apicomida.models.DTO.FoodDTO;
import com.demoapi.apicomida.services.FoodService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/food")
public class FoodController {
    private final FoodService foodService;

    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @PostMapping
    public ResponseEntity<FoodDTO> createFood(@Valid @RequestBody FoodDTO foodDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(foodService.createFood(foodDTO));
    }

    @GetMapping
    public ResponseEntity<List<FoodDTO>> getFoods(@RequestParam(required = false) String name) {
        return ResponseEntity.ok(foodService.getFoods(name));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodDTO> getFoodById(@PathVariable Long id) {
        return ResponseEntity.ok(foodService.getFoodById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoodDTO> updateFood(@PathVariable Long id, @Valid @RequestBody FoodDTO foodDTO) {
        return ResponseEntity.ok(foodService.updateFood(id, foodDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFood(@PathVariable Long id) {
        foodService.deleteFood(id);
        return ResponseEntity.noContent().build();
    }
}
