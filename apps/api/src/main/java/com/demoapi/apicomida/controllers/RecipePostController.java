package com.demoapi.apicomida.controllers;

import com.demoapi.apicomida.dtos.RecipePostDtos.RecipePostRequest;
import com.demoapi.apicomida.services.RecipePostService;
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
public class RecipePostController {

    private final RecipePostService recipePostService;

    public RecipePostController(RecipePostService recipePostService) {
        this.recipePostService = recipePostService;
    }

    @PostMapping("/recipe-posts")
    public Object create(@Valid @RequestBody RecipePostRequest request) {
        return recipePostService.create(request);
    }

    @GetMapping("/recipe-posts")
    public Object getAll() {
        return recipePostService.getAll();
    }

    @GetMapping("/recipe-posts/{id}")
    public Object getById(@PathVariable UUID id) {
        return recipePostService.getById(id);
    }

    @PatchMapping("/recipe-posts/{id}")
    public Object update(@PathVariable UUID id, @Valid @RequestBody RecipePostRequest request) {
        return recipePostService.update(id, request);
    }

    @DeleteMapping("/recipe-posts/{id}")
    public Object delete(@PathVariable UUID id) {
        recipePostService.delete(id);
        return ApiResponses.message("Recipe post deleted");
    }

    @PostMapping("/recipe-posts/{id}/like")
    public Object like(@PathVariable UUID id) {
        return recipePostService.like(id);
    }

    @DeleteMapping("/recipe-posts/{id}/like")
    public Object unlike(@PathVariable UUID id) {
        return recipePostService.unlike(id);
    }
}
