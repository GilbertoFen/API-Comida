package com.demoapi.apicomida.controllers;

import com.demoapi.apicomida.dtos.WorkoutDtos.WorkoutPlanDayRequest;
import com.demoapi.apicomida.dtos.WorkoutDtos.WorkoutPlanExerciseRequest;
import com.demoapi.apicomida.dtos.WorkoutDtos.WorkoutPlanRequest;
import com.demoapi.apicomida.services.WorkoutPlanService;
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
public class WorkoutPlanController {

    private final WorkoutPlanService workoutPlanService;

    public WorkoutPlanController(WorkoutPlanService workoutPlanService) {
        this.workoutPlanService = workoutPlanService;
    }

    @PostMapping("/workout-plans")
    public Object create(@Valid @RequestBody WorkoutPlanRequest request) {
        return workoutPlanService.create(request);
    }

    @GetMapping("/workout-plans/me")
    public Object getMine() {
        return workoutPlanService.getMine();
    }

    @GetMapping("/workout-plans/{id}")
    public Object getById(@PathVariable UUID id) {
        return workoutPlanService.getById(id);
    }

    @PatchMapping("/workout-plans/{id}")
    public Object update(@PathVariable UUID id, @Valid @RequestBody WorkoutPlanRequest request) {
        return workoutPlanService.update(id, request);
    }

    @DeleteMapping("/workout-plans/{id}")
    public Object delete(@PathVariable UUID id) {
        workoutPlanService.delete(id);
        return ApiResponses.message("Workout plan deleted");
    }

    @PostMapping("/workout-plans/{id}/days")
    public Object addDay(@PathVariable UUID id, @Valid @RequestBody WorkoutPlanDayRequest request) {
        return workoutPlanService.addDay(id, request);
    }

    @GetMapping("/workout-plans/{id}/days")
    public Object getDays(@PathVariable UUID id) {
        return workoutPlanService.getDays(id);
    }

    @PatchMapping("/workout-plan-days/{dayId}")
    public Object updateDay(@PathVariable UUID dayId, @Valid @RequestBody WorkoutPlanDayRequest request) {
        return workoutPlanService.updateDay(dayId, request);
    }

    @DeleteMapping("/workout-plan-days/{dayId}")
    public Object deleteDay(@PathVariable UUID dayId) {
        workoutPlanService.deleteDay(dayId);
        return ApiResponses.message("Workout plan day deleted");
    }

    @PostMapping("/workout-plan-days/{dayId}/exercises")
    public Object addExercise(@PathVariable UUID dayId, @Valid @RequestBody WorkoutPlanExerciseRequest request) {
        return workoutPlanService.addExercise(dayId, request);
    }

    @PatchMapping("/workout-plan-exercises/{id}")
    public Object updateExercise(@PathVariable UUID id, @Valid @RequestBody WorkoutPlanExerciseRequest request) {
        return workoutPlanService.updateExercise(id, request);
    }

    @DeleteMapping("/workout-plan-exercises/{id}")
    public Object deleteExercise(@PathVariable UUID id) {
        workoutPlanService.deleteExercise(id);
        return ApiResponses.message("Workout plan exercise deleted");
    }
}
