package com.demoapi.apicomida.controllers;

import com.demoapi.apicomida.dtos.ExerciseDtos.ExerciseRequest;
import com.demoapi.apicomida.services.ExerciseService;
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
public class ExerciseController {

    private final ExerciseService exerciseService;

    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @GetMapping("/exercises")
    public Object getAll() {
        return exerciseService.getAll();
    }

    @GetMapping("/exercises/{id}")
    public Object getById(@PathVariable UUID id) {
        return exerciseService.getById(id);
    }

    @GetMapping("/exercises/search")
    public Object search(@RequestParam String query) {
        return exerciseService.search(query);
    }

    @GetMapping("/exercises/muscle-group/{muscleGroup}")
    public Object byMuscleGroup(@PathVariable String muscleGroup) {
        return exerciseService.getByMuscleGroup(muscleGroup);
    }

    @PostMapping("/exercises")
    public Object create(@Valid @RequestBody ExerciseRequest request) {
        return exerciseService.create(request);
    }

    @PatchMapping("/exercises/{id}")
    public Object update(@PathVariable UUID id, @Valid @RequestBody ExerciseRequest request) {
        return exerciseService.update(id, request);
    }

    @DeleteMapping("/exercises/{id}")
    public Object delete(@PathVariable UUID id) {
        exerciseService.delete(id);
        return ApiResponses.message("Exercise deleted");
    }
}
