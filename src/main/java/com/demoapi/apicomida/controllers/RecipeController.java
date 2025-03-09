package com.demoapi.apicomida.controllers;

import com.demoapi.apicomida.models.DTO.RecipeDTO.CreateRecipeDTO;
import com.demoapi.apicomida.models.DTO.RecipeDTO.RecipeDTO;
import com.demoapi.apicomida.services.RecipeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "http://localhost:63342")
@RestController
@RequestMapping("/api/recipes")
public class RecipeController {
    private RecipeService recipeService;
    @Autowired
    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @PostMapping
    public ResponseEntity<?> createRecipe(@RequestBody @Valid CreateRecipeDTO recipeCreateDTO) {
        System.out.println("Datos recibidos: " + recipeCreateDTO);
        return recipeService.createRecipe(recipeCreateDTO);
    }

    @GetMapping
    public ResponseEntity<List<RecipeDTO>> getAllRecipes() {
        return recipeService.getAllRecipes();
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
