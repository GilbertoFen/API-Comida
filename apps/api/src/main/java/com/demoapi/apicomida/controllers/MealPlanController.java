package com.demoapi.apicomida.controllers;

import com.demoapi.apicomida.dtos.MealPlanDtos.MealPlanDayRequest;
import com.demoapi.apicomida.dtos.MealPlanDtos.MealPlanMealRequest;
import com.demoapi.apicomida.dtos.MealPlanDtos.MealPlanRequest;
import com.demoapi.apicomida.services.MealPlanService;
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
public class MealPlanController {

    private final MealPlanService mealPlanService;

    public MealPlanController(MealPlanService mealPlanService) {
        this.mealPlanService = mealPlanService;
    }

    @PostMapping("/meal-plans")
    public Object create(@Valid @RequestBody MealPlanRequest request) {
        return mealPlanService.create(request);
    }

    @GetMapping("/meal-plans/me")
    public Object getMine() {
        return mealPlanService.getMine();
    }

    @GetMapping("/meal-plans/{id}")
    public Object getById(@PathVariable UUID id) {
        return mealPlanService.getById(id);
    }

    @PatchMapping("/meal-plans/{id}")
    public Object update(@PathVariable UUID id, @Valid @RequestBody MealPlanRequest request) {
        return mealPlanService.update(id, request);
    }

    @DeleteMapping("/meal-plans/{id}")
    public Object delete(@PathVariable UUID id) {
        mealPlanService.delete(id);
        return ApiResponses.message("Meal plan deleted");
    }

    @PostMapping("/meal-plans/{id}/days")
    public Object addDay(@PathVariable UUID id, @Valid @RequestBody MealPlanDayRequest request) {
        return mealPlanService.addDay(id, request);
    }

    @GetMapping("/meal-plans/{id}/days")
    public Object getDays(@PathVariable UUID id) {
        return mealPlanService.getDays(id);
    }

    @PatchMapping("/meal-plan-days/{dayId}")
    public Object updateDay(@PathVariable UUID dayId, @Valid @RequestBody MealPlanDayRequest request) {
        return mealPlanService.updateDay(dayId, request);
    }

    @DeleteMapping("/meal-plan-days/{dayId}")
    public Object deleteDay(@PathVariable UUID dayId) {
        mealPlanService.deleteDay(dayId);
        return ApiResponses.message("Meal plan day deleted");
    }

    @PostMapping("/meal-plan-days/{dayId}/meals")
    public Object addMeal(@PathVariable UUID dayId, @Valid @RequestBody MealPlanMealRequest request) {
        return mealPlanService.addMeal(dayId, request);
    }

    @PatchMapping("/meal-plan-meals/{mealId}")
    public Object updateMeal(@PathVariable UUID mealId, @Valid @RequestBody MealPlanMealRequest request) {
        return mealPlanService.updateMeal(mealId, request);
    }

    @DeleteMapping("/meal-plan-meals/{mealId}")
    public Object deleteMeal(@PathVariable UUID mealId) {
        mealPlanService.deleteMeal(mealId);
        return ApiResponses.message("Meal plan meal deleted");
    }

    @PostMapping("/meal-plans/{id}/analyze")
    public Object analyze(@PathVariable UUID id) {
        return mealPlanService.analyze(id);
    }
}
