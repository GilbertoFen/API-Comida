package com.demoapi.apicomida.controllers;

import com.demoapi.apicomida.dtos.WorkoutDtos.WorkoutLogExerciseRequest;
import com.demoapi.apicomida.dtos.WorkoutDtos.WorkoutLogRequest;
import com.demoapi.apicomida.services.WorkoutLogService;
import com.demoapi.apicomida.util.ApiResponses;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WorkoutLogController {

    private final WorkoutLogService workoutLogService;

    public WorkoutLogController(WorkoutLogService workoutLogService) {
        this.workoutLogService = workoutLogService;
    }

    @PostMapping("/workout-logs")
    public Object create(@Valid @RequestBody WorkoutLogRequest request) {
        return workoutLogService.create(request);
    }

    @GetMapping("/workout-logs")
    public Object getByDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return workoutLogService.getByDate(date);
    }

    @GetMapping("/workout-logs/range")
    public Object getByRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return workoutLogService.getByRange(startDate, endDate);
    }

    @GetMapping("/workout-logs/summary")
    public Object summary() {
        return workoutLogService.getSummary();
    }

    @GetMapping("/workout-logs/{id}")
    public Object getById(@PathVariable UUID id) {
        return workoutLogService.getById(id);
    }

    @PatchMapping("/workout-logs/{id}")
    public Object update(@PathVariable UUID id, @Valid @RequestBody WorkoutLogRequest request) {
        return workoutLogService.update(id, request);
    }

    @DeleteMapping("/workout-logs/{id}")
    public Object delete(@PathVariable UUID id) {
        workoutLogService.delete(id);
        return ApiResponses.message("Workout log deleted");
    }

    @PostMapping("/workout-logs/{id}/exercises")
    public Object addExercise(@PathVariable UUID id, @Valid @RequestBody WorkoutLogExerciseRequest request) {
        return workoutLogService.addExercise(id, request);
    }

    @PatchMapping("/workout-log-exercises/{id}")
    public Object updateExercise(@PathVariable UUID id, @Valid @RequestBody WorkoutLogExerciseRequest request) {
        return workoutLogService.updateExercise(id, request);
    }

    @DeleteMapping("/workout-log-exercises/{id}")
    public Object deleteExercise(@PathVariable UUID id) {
        workoutLogService.deleteExercise(id);
        return ApiResponses.message("Workout log exercise deleted");
    }
}
