package com.demoapi.apicomida.controllers;

import com.demoapi.apicomida.dtos.RecipeDtos.RecipeIngredientRequest;
import com.demoapi.apicomida.dtos.RecipeDtos.RecipeRequest;
import com.demoapi.apicomida.services.RecipeService;
import com.demoapi.apicomida.util.ApiResponses;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @PostMapping("/recipes")
    public Object create(@Valid @RequestBody RecipeRequest request) {
        return recipeService.create(request);
    }

    @GetMapping("/recipes")
    public Object getAll() {
        return recipeService.getAll();
    }

    @GetMapping("/recipes/{id}")
    public Object getById(@PathVariable UUID id) {
        return recipeService.getById(id);
    }

    @GetMapping("/recipes/public")
    public Object getPublic() {
        return recipeService.getPublicRecipes();
    }

    @GetMapping("/recipes/me")
    public Object getMine() {
        return recipeService.getMine();
    }

    @PatchMapping("/recipes/{id}")
    public Object update(@PathVariable UUID id, @Valid @RequestBody RecipeRequest request) {
        return recipeService.update(id, request);
    }

    @DeleteMapping("/recipes/{id}")
    public Object delete(@PathVariable UUID id) {
        recipeService.delete(id);
        return ApiResponses.message("Recipe deleted");
    }

    @PostMapping("/recipes/{id}/ingredients")
    public Object addIngredient(@PathVariable UUID id, @Valid @RequestBody RecipeIngredientRequest request) {
        return recipeService.addIngredient(id, request);
    }

    @PatchMapping("/recipes/{id}/ingredients/{ingredientId}")
    public Object updateIngredient(
            @PathVariable UUID id,
            @PathVariable UUID ingredientId,
            @Valid @RequestBody RecipeIngredientRequest request
    ) {
        return recipeService.updateIngredient(id, ingredientId, request);
    }

    @DeleteMapping("/recipes/{id}/ingredients/{ingredientId}")
    public Object deleteIngredient(@PathVariable UUID id, @PathVariable UUID ingredientId) {
        recipeService.deleteIngredient(id, ingredientId);
        return ApiResponses.message("Recipe ingredient deleted");
    }

    @PostMapping("/recipes/{id}/calculate-nutrition")
    public Object calculateNutrition(@PathVariable UUID id) {
        return recipeService.calculateNutrition(id);
    }
}
