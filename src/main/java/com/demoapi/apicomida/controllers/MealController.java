package com.demoapi.apicomida.controllers;

import com.demoapi.apicomida.models.DTO.MealDTO;
import com.demoapi.apicomida.services.MealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/meals")
public class MealController {
    private final MealService mealService;

    @Autowired
    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    @PostMapping
    public ResponseEntity<?> createMeal(@RequestBody MealDTO mealDTO) {
        return mealService.createMeal(mealDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMealById(@PathVariable long id) {
        return mealService.getMealById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMeal(@PathVariable long id, @RequestBody MealDTO mealDTO) {
        return mealService.updateMeal(id, mealDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMeal(@PathVariable long id) {
        return mealService.deleteMeal(id);
    }

}
