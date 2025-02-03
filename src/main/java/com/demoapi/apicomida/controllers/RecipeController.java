package com.demoapi.apicomida.controllers;

import com.demoapi.apicomida.models.DTO.RecipeDTO;
import com.demoapi.apicomida.services.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/recipes")
public class RecipeController {
    private RecipeService recipeService;
    @Autowired
    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @PostMapping
    public ResponseEntity<?> createRecipe(@RequestBody RecipeDTO recipeDTO) {
        return recipeService.createRecipe(recipeDTO);
    }
    @GetMapping
    public ResponseEntity<List<RecipeDTO>> getAllRecipes() {
        List<RecipeDTO> recipes = recipeService.getAllRecipes();
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeDTO> getRecipeById(@PathVariable Long id) {
        return recipeService.getRecipeById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRecipe(@PathVariable Long id, @RequestBody RecipeDTO recipeDTO) {
        return recipeService.updateRecipe(id, recipeDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecipe(@PathVariable Long id) {
        return recipeService.deleteRecipe(id);
    }

}
