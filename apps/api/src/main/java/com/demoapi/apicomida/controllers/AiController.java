package com.demoapi.apicomida.controllers;

import com.demoapi.apicomida.dtos.AiDtos.EstimateCaloriesRequest;
import com.demoapi.apicomida.dtos.AiDtos.GenericAiRequest;
import com.demoapi.apicomida.services.AiService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/ai/estimate-calories")
    public Object estimateCalories(@Valid @RequestBody EstimateCaloriesRequest request) {
        return aiService.estimateCalories(request);
    }

    @PostMapping("/ai/generate-meal-plan")
    public Object generateMealPlan(@RequestBody GenericAiRequest request) {
        return aiService.generateMealPlan(request);
    }

    @PostMapping("/ai/generate-workout-plan")
    public Object generateWorkoutPlan(@RequestBody GenericAiRequest request) {
        return aiService.generateWorkoutPlan(request);
    }

    @PostMapping("/ai/recommend-recipes")
    public Object recommendRecipes(@RequestBody GenericAiRequest request) {
        return aiService.recommendRecipes(request);
    }

    @PostMapping("/ai/analyze-progress")
    public Object analyzeProgress(@RequestBody GenericAiRequest request) {
        return aiService.analyzeProgress(request);
    }

    @GetMapping("/ai/history")
    public Object history() {
        return aiService.getHistory();
    }

    @GetMapping("/ai/history/{id}")
    public Object historyById(@PathVariable UUID id) {
        return aiService.getById(id);
    }
}
