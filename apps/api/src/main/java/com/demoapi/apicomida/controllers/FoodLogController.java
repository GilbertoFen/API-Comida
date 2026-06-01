package com.demoapi.apicomida.controllers;

import com.demoapi.apicomida.dtos.FoodLogDtos.FoodLogRequest;
import com.demoapi.apicomida.services.FoodLogService;
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
public class FoodLogController {

    private final FoodLogService foodLogService;

    public FoodLogController(FoodLogService foodLogService) {
        this.foodLogService = foodLogService;
    }

    @PostMapping("/food-logs")
    public Object create(@Valid @RequestBody FoodLogRequest request) {
        return foodLogService.create(request);
    }

    @GetMapping("/food-logs")
    public Object getByDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return foodLogService.getByDate(date);
    }

    @GetMapping("/food-logs/range")
    public Object getByRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return foodLogService.getByRange(startDate, endDate);
    }

    @GetMapping("/food-logs/summary")
    public Object summary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return foodLogService.getSummary(date);
    }

    @PatchMapping("/food-logs/{id}")
    public Object update(@PathVariable UUID id, @Valid @RequestBody FoodLogRequest request) {
        return foodLogService.update(id, request);
    }

    @DeleteMapping("/food-logs/{id}")
    public Object delete(@PathVariable UUID id) {
        foodLogService.delete(id);
        return ApiResponses.message("Food log deleted");
    }
}
