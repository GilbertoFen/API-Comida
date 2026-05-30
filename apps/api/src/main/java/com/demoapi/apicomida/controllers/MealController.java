package com.demoapi.apicomida.controllers;

import com.demoapi.apicomida.models.DTO.MealDTO;
import com.demoapi.apicomida.services.MealService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/meals")
public class MealController {
    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    @PostMapping
    public ResponseEntity<MealDTO> createMeal(@Valid @RequestBody MealDTO mealDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mealService.createMeal(mealDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MealDTO> getMealById(@PathVariable long id) {
        return ResponseEntity.ok(mealService.getMealById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeal(@PathVariable long id) {
        mealService.deleteMeal(id);
        return ResponseEntity.noContent().build();
    }
}
