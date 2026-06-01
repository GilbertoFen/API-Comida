package com.demoapi.apicomida.controllers;

import com.demoapi.apicomida.dtos.FoodDtos.CustomFoodRequest;
import com.demoapi.apicomida.dtos.FoodDtos.FoodRequest;
import com.demoapi.apicomida.services.FoodService;
import com.demoapi.apicomida.util.ApiResponses;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FoodController {

    private final FoodService foodService;

    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @GetMapping("/foods")
    public Object getFoods() {
        return foodService.getFoods();
    }

    @GetMapping("/foods/{id}")
    public Object getFood(@PathVariable UUID id) {
        return foodService.getFood(id);
    }

    @PostMapping("/foods")
    public Object create(@Valid @RequestBody FoodRequest request) {
        return foodService.create(request);
    }

    @PatchMapping("/foods/{id}")
    public Object update(@PathVariable UUID id, @Valid @RequestBody FoodRequest request) {
        return foodService.update(id, request);
    }

    @DeleteMapping("/foods/{id}")
    public Object delete(@PathVariable UUID id) {
        foodService.delete(id);
        return ApiResponses.message("Food deleted");
    }

    @GetMapping("/foods/search")
    public Object search(@RequestParam String query) {
        return foodService.searchFoods(query);
    }

    @GetMapping("/foods/barcode/{barcode}")
    public Object barcode(@PathVariable String barcode) {
        return foodService.findByBarcode(barcode);
    }

    @GetMapping("/foods/category/{category}")
    public Object category(@PathVariable String category) {
        return foodService.getByCategory(category);
    }

    @PostMapping("/foods/custom")
    public Object createCustom(@Valid @RequestBody CustomFoodRequest request) {
        return foodService.createCustomFood(request);
    }
}
