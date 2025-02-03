package com.demoapi.apicomida.services;

import com.demoapi.apicomida.models.DTO.RecipeDTO;
import com.demoapi.apicomida.models.RecipeModel;
import com.demoapi.apicomida.repositories.RecipeRepository;
import com.demoapi.apicomida.util.Mappers.RecipeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;
    @Autowired
    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public ResponseEntity<?> createRecipe(RecipeDTO recipeDTO) {
        try {
            RecipeModel recipeModel = RecipeMapper.toModel(recipeDTO);
            recipeRepository.save(recipeModel);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating recipe");
        }
    }

    public List<RecipeDTO> getAllRecipes() {
        List<RecipeModel> recipes = recipeRepository.findAll();
        return recipes.stream()
                .map(RecipeMapper::toDTO) // Usando el RecipeMapper
                .collect(Collectors.toList());
    }
    public ResponseEntity<RecipeDTO> getRecipeById(Long id) {
        Optional<RecipeModel> recipeModel = recipeRepository.findById(id);
        return recipeModel.map(model -> ResponseEntity
                .ok(RecipeMapper.
                        toDTO(model))).
                orElseGet(() -> ResponseEntity.
                        status(HttpStatus.NOT_FOUND).
                        build());
    }

    public ResponseEntity<?> updateRecipe(Long id, RecipeDTO recipeDTO) {
        Optional<RecipeModel> existingRecipe = recipeRepository.findById(id);
        if (existingRecipe.isPresent()) {
            RecipeModel updatedRecipe = RecipeMapper.toModel(recipeDTO);
            updatedRecipe.setId(id);
            recipeRepository.save(updatedRecipe);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recipe not found");
    }

    public ResponseEntity<?> deleteRecipe(Long id) {
        if (recipeRepository.existsById(id)) {
            recipeRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recipe not found");
    }

}
